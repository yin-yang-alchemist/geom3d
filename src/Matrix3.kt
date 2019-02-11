import kotlin.math.*

/**
 * 3x3の行列
 * @property i      1行目を表すベクトル
 * @property j      2行目を表すベクトル
 * @property k      3行目を表すベクトル
 * @property T      転置行列
 * @property det    行列式
 * @property inv    逆行列
 * @property isOrthogonal   正規直交行列かどうかの判定
 */
data class Matrix3(val i: Vector3, val j: Vector3, val k: Vector3) {

    // プロパティ
    val T: Matrix3 by lazy { transpose() }
    val det: Double by lazy { computeDeterminant() }
    val inv: Matrix3? by lazy { computeInverse() }
    val isOrthogonal: Boolean by lazy { checkOrthogonal() }

    // 演算子のオーバーロード
    operator fun plus(other: Matrix3) = Matrix3(i + other.i, j + other.j, k + other.k)

    operator fun minus(other: Matrix3) = Matrix3(i - other.i, j - other.j, k - other.k)
    operator fun times(scalar: Double) = Matrix3(i * scalar, j * scalar, k * scalar)
    operator fun times(vec: Vector3) = Vector3(i dot vec, j dot vec, k dot vec)
    operator fun times(other: Matrix3): Matrix3 {
        val (col1, col2, col3) = other.transpose()
        return Matrix3(
            Vector3(i dot col1, i dot col2, i dot col3),
            Vector3(j dot col1, j dot col2, j dot col3),
            Vector3(k dot col1, k dot col2, k dot col3)
        )
    }

    /** 転置行列の計算 */
    private fun transpose() = Matrix3(Vector3(i.x, j.x, k.x), Vector3(i.y, j.y, k.y), Vector3(i.z, j.z, k.z))

    /** 行列式の計算 */
    private fun computeDeterminant(): Double =
        i.x * j.y * k.z + i.y * j.z * k.x + i.z * j.x * k.y - i.z * j.y * k.x - i.x * j.z * k.y - i.y * j.x * k.z

    /** 逆行列の計算 */
    private fun computeInverse(): Matrix3? = when {
        det.isClose(0.0) -> null
        else ->
            Matrix3(
                Vector3(j.y * k.z - j.z * k.y, -(i.y * k.z - i.z * k.y), i.y * j.z - i.z * j.y),
                Vector3(-(j.x * k.z - j.z * k.x), i.x * k.z - i.z * k.x, -(i.x * j.z - i.z * j.x)),
                Vector3(j.x * k.y - j.y * k.x, -(i.x * k.y - i.y * k.x), i.x * j.y - i.y * j.x)
            ) * (1 / det)
    }

    /** 正規直交行列になっているかどうかのチェック */
    private fun checkOrthogonal(): Boolean {
        return i.isUnit && j.isUnit && k.isUnit
                && (i dot j).isClose(0.0) && (j dot k).isClose(0.0 ) && (k dot i).isClose(0.0)
    }

    /** ２つの行列が等しいことを判定する */
    fun isClose(other: Matrix3) = i.isClose(other.i) && j.isClose(other.j) && k.isClose(other.k)

    /** リストに変換する */
    fun toList(): List<List<Double>> = listOf(i.toList(), j.toList(), k.toList())

    override fun toString(): String =
        listOf(i, j, k).joinToString("\n ", "[", "]")

    /**
     * 行列の生成に使う機能
     */
    companion object {
        val zero = Matrix3(Vector3.zero, Vector3.zero, Vector3.zero)
        val identity = Matrix3(Vector3.unitX, Vector3.unitY, Vector3.unitZ)
        fun random() = Matrix3(Vector3.random(), Vector3.random(), Vector3.random())

        /**
         * 対角行列を作成する。
         */
        fun createDiag(x: Double, y: Double, z: Double) =
            Matrix3(Vector3.unitX * x, Vector3.unitY * y, Vector3.unitZ * z)

        /**
         * 方向余弦行列を作成する。
         */
        fun createCsys(i: Vector3, j: Vector3): Matrix3 {
            val jOrtho = j - i.unit * (i.unit dot j)    // iに垂直になるように修正したj
            val k = i.unit cross jOrtho.unit
            return Matrix3(i.unit, jOrtho.unit, k)
        }

        /**
         * 回転軸と回転角度から回転行列を作成する。
         * [ロドリゲスの回転公式 - Wikipedia](https://ja.wikipedia.org/wiki/ロドリゲスの回転公式)
         */
        fun createRotation(axis: Vector3, theta: Double): Matrix3? {
            if (axis.isClose(Vector3.zero)) {
                return null
            } else {
                val (x, y, z) = axis.unit
                val cosTheta = cos(theta)
                val sinTheta = sin(theta)
                val i = Vector3(
                    cosTheta + x * x * (1 - cosTheta),
                    x * y * (1 - cosTheta) - z * sinTheta,
                    z * x * (1 - cosTheta) + y * sinTheta
                )
                val j = Vector3(
                    x * y * (1 - cosTheta) + z * sinTheta,
                    cosTheta + y * y * (1 - cosTheta),
                    y * z * (1 - cosTheta) - x * sinTheta
                )
                val k = Vector3(
                    z * x * (1 - cosTheta) - y * sinTheta,
                    y * z * (1 - cosTheta) + x * sinTheta,
                    cosTheta + z * z * (1 - cosTheta)
                )
                return Matrix3(i, j, k)
            }
        }
    }
}
