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
    operator fun plus(other: Quaternion) =
        Quaternion(x + other.x, y + other.y, z + other.z, w + other.w)

    operator fun times(k: Double) =
        Quaternion(k * x, k * y, k * z, k * w)

    operator fun div(k: Double) =
        Quaternion(x / k, y / k, z / k, w / k)

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

    // TODO: ノルムが１ではない場合を顧慮する
    /** 回転行列に変換する */
    fun toMatrix(): Matrix3 =
        Matrix3(
            Vector3(x * x - y * y - z * z + w * w, 2 * (x * y - z * w), 2 * (x * z + y * w)),
            Vector3(2 * (x * y + z * w), -x * x + y * y - z * z + w * w, 2 * (y * z - x * w)),
            Vector3(2 * (x * z - y * x), 2 * (x * y + z * w), -x * x - y * y + z * z + w * w)
        )

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

        // TODO: 未実装
        /**
         * 回転行列からクォータニオンに変換する
         */
        fun createFromMatrix(mat: Matrix3): Quaternion? {
            throw  NotImplementedError()
        }
    }
}

