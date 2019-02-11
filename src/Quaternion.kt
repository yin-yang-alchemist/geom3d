import kotlin.math.*

/**
 * クォータニオン
 * @property x      第１成分
 * @property y      第２成分
 * @property z      第３成分
 * @property w      第４成分
 * @property isUnit 単位クォータニオンかどうかの判定
 * @property norm   ノルム
 * @property inv    逆元
 * @property angle  回転角度
 * @property axis   回転軸
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

    /** 演算子のオーバーロード Quaternion * Double */
    operator fun times(k: Double) = Quaternion(k * x, k * y, k * z, k * w)

    /** 演算子のオーバーロード Quaternion / Double */
    operator fun div(k: Double) = Quaternion(x / k, y / k, z / k, w / k)

    /** 演算子のオーバーロード Quaternion * Quaternion */
    operator fun times(other: Quaternion): Quaternion {
        val (x1, y1, z1, w1) = this
        val (x2, y2, z2, w2) = other
        return Quaternion(
            w1 * x2 + x1 * w2 + y1 * z2 - z1 * y2,
            w1 * y2 - x1 * z2 + y1 * w2 + z1 * x2,
            w1 * z2 + x1 * y2 - y1 * x2 + z1 * w2,
            w1 * w2 - x1 * x2 - y1 * y2 - z1 * z2
        )
    }

    /** 回転軸の計算 */
    private fun computeAxis(): Vector3 {
        val sinThetaHalf = sqrt(1 - unit.w * unit.w)
        return Vector3(unit.x / sinThetaHalf, unit.y / sinThetaHalf, unit.z / sinThetaHalf)
    }

    /** ２つのクォータニオンが等しいことを判定する */
    fun isClose(other: Quaternion) =
        x.isClose(other.x) && y.isClose(other.y) && z.isClose(other.z) && w.isClose(other.w)

    /** ２つのクォータニオンが等価であることを判定する */
    fun isEquivalent(other: Quaternion) = this.isClose(other) || this.isClose(other * (-1.0))

    /** 回転行列に変換する */
    fun toMatrix(): Matrix3 {
        val (x, y, z, w) = this.unit
        return Matrix3(
            Vector3(x * x - y * y - z * z + w * w, 2 * (x * y - z * w), 2 * (x * z + y * w)),
            Vector3(2 * (x * y + z * w), -x * x + y * y - z * z + w * w, 2 * (y * z - x * w)),
            Vector3(2 * (x * z - y * w), 2 * (y * z + x * w), -x * x - y * y + z * z + w * w)
        )
    }

    /** リストに変換する */
    fun toList() = listOf(x, y, z, w)

    override fun toString(): String = "[%7.4f, %7.4f, %7.4f, %7.4f]".format(x, y, z, w)

    /**
     * クォータニオンの生成に使う機能
     */
    companion object {
        val identity = Quaternion(0.0, 0.0, 0.0, 1.0)

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
                    val sinThetaHalf = sin(theta / 2)
                    Quaternion(x * sinThetaHalf, y * sinThetaHalf, z * sinThetaHalf, cos(theta / 2))
                }
            }

        /**
         * 回転行列からクォータニオンに変換する
         * [MATLAB によるクォータニオン数値計算](http://www.mss.co.jp/technology/report/pdf/19-08.pdf)
         * この資料とは行と列が逆になっていることに注意
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
                        (mat.j.x + mat.i.y) / (4 * q1),
                        (mat.k.x + mat.i.z) / (4 * q1),
                        (mat.k.y - mat.j.z) / (4 * q1)
                    )
                    1 -> Quaternion(
                        (mat.j.x + mat.i.y) / (4 * q2),
                        q2,
                        (mat.j.z + mat.k.y) / (4 * q2),
                        (mat.i.z - mat.k.x) / (4 * q2)
                    )
                    2 -> Quaternion(
                        (mat.i.z + mat.k.x) / (4 * q3),
                        (mat.j.z + mat.k.y) / (4 * q3),
                        q3,
                        (mat.j.x - mat.i.y) / (4 * q3)
                    )
                    else -> Quaternion(
                        (mat.k.y - mat.j.z) / (4 * q4),
                        (mat.i.z - mat.k.x) / (4 * q4),
                        (mat.j.x - mat.i.y) / (4 * q4),
                        q4
                    )
                }
            }
        }
    }
}
