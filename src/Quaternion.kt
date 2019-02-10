import kotlin.math.*

/**
 * クォータニオン
 */
data class Quaternion(val x: Double, val y: Double, val z: Double, val w: Double) {

    // プロパティ
    private val normSquared by lazy { x * x + y * y + z * z + w * w }
    val isUnit: Boolean by lazy { normSquared.isClose(1.0) }
    val norm: Double by lazy { sqrt(normSquared) }
    val unit: Quaternion by lazy { if (isUnit) this else this / norm }
    val inv: Quaternion by lazy { Quaternion(-x, -y, -z, w) / normSquared }
    val angle: Double by lazy { acos(unit.w) * 2 }
    val axis: Vector3 by lazy { computeAxis() }

    constructor(vec: Vector3, w: Double) : this(vec.x, vec.y, vec.z, w)

    // 演算子のオーバーロード
    operator fun times(k: Double) = Quaternion(k * x, k * y, k * z, k * w)
    operator fun div(k: Double) = Quaternion(x / k, y / k, z / k, w / k)
    operator fun times(other: Quaternion): Quaternion {
        val vec1 = Vector3(this.x, this.y, this.z)
        val vec2 = Vector3(other.x, other.y, other.z)
        return Quaternion(
            vec1 cross vec2 + vec1 * other.w + vec2 * this.w,
            this.w * other.w - (vec1 dot vec2)
        )
    }

    /** 回転軸の計算 */
    private fun computeAxis(): Vector3 {
        val sin_theta_half = sqrt(1 - unit.w * unit.w)
        return Vector3(unit.x / sin_theta_half, unit.y / sin_theta_half, unit.z / sin_theta_half)
    }

    /** 回転行列に変換する */
    fun toMatrix(): Matrix3 {
        val (x, y, z, w) = this.unit
        return Matrix3(
            Vector3(x * x - y * y - z * z + w * w, 2 * (x * y - z * w), 2 * (x * z + y * w)),
            Vector3(2 * (x * y + z * w), -x * x + y * y - z * z + w * w, 2 * (y * z - x * w)),
            Vector3(2 * (x * z - y * x), 2 * (x * y + z * w), -x * x - y * y + z * z + w * w)
        )
    }

    /**
     * クォータニオンの生成に使う機能
     */
    companion object {
        /**
         * 単位クォータニオンを作成する。
         */
        fun createUnit(x: Double, y: Double, z: Double, w: Double): Quaternion {
            val norm = sqrt(x * x + y * y + z * z + w * w)
            return Quaternion(x / norm, y / norm, z / norm, w / norm)
        }

        /**
         * 回転軸と回転角度からクォータニオンを作成する。
         */
        fun createRotation(axis: Vector3, theta: Double): Quaternion? =
            when {
                axis.isClose(Vector3.zero) -> null
                else -> {
                    val (x, y, z) = axis.unit
                    val sin_theta_half = sin(theta / 2)
                    Quaternion(x * sin_theta_half, y * sin_theta_half, z * sin_theta_half, cos(theta / 2))
                }
            }

        /**
         * 回転行列からクォータニオンに変換する
         * [MATLAB によるクォータニオン数値計算](http://www.mss.co.jp/technology/report/pdf/19-08.pdf)
         */
        fun createFromMatrix(mat: Matrix3): Quaternion? {
            if (!mat.isOrthogonal) {
                return null
            } else {
                val q1 = sqrt(1.0 + mat.i.x - mat.j.y - mat.k.z) / 2
                val q2 = sqrt(1.0 - mat.i.x + mat.j.y - mat.k.z) / 2
                val q3 = sqrt(1.0 - mat.i.x - mat.j.y + mat.k.z) / 2
                val q4 = sqrt(1.0 + mat.i.x + mat.j.y + mat.k.z) / 2
                val maxIndex = (0..3).maxBy { listOf(q1, q2, q3, q4)[it] }
                return when (maxIndex) {
                    0 -> Quaternion(
                        q1,
                        (mat.i.y + mat.j.x) / (4 * q1),
                        (mat.i.z + mat.k.x) / (4 * q1),
                        (mat.j.z - mat.k.y) / (4 * q1)
                    )
                    1 -> Quaternion(
                        (mat.i.y + mat.j.x) / (4 * q2),
                        q2,
                        (mat.k.y + mat.j.z) / (4 * q2),
                        (mat.k.x - mat.i.z) / (4 * q2)
                    )
                    2 -> Quaternion(
                        (mat.k.x + mat.i.z) / (4 * q3),
                        (mat.k.y + mat.j.z) / (4 * q3),
                        q3,
                        (mat.i.y - mat.j.x) / (4 * q3)
                    )
                    else -> Quaternion(
                        (mat.j.z - mat.k.y) / (4 * q4),
                        (mat.k.x - mat.i.z) / (4 * q4),
                        (mat.i.y - mat.j.x) / (4 * q4),
                        q4
                    )
                }
            }
        }
    }
}

