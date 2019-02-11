import kotlin.math.*

/**
 * 3x3の行列
 * @property i      1列目を表すベクトル
 * @property j      2列目を表すベクトル
 * @property k      3列目を表すベクトル
 * @property rows   3つの行を表すベクトル
 * @property det    行列式
 * @property T      転置行列
 * @property inv    逆行列
 * @property isOrthonormal  正規直交行列かどうかの判定
 */
data class Matrix3(val i: Vector3, val j: Vector3, val k: Vector3) {

    // プロパティ
    val rows: List<Vector3> by lazy { toRows() }
    val det: Double by lazy { computeDeterminant() }
    val T: Matrix3 by lazy { transpose() }
    val inv: Matrix3? by lazy { computeInverse() }
    val isOrthonormal: Boolean by lazy { checkOrthonormal() }

    /** 演算子のオーバーロード Matrix3 + Matrix3 */
    operator fun plus(other: Matrix3) = Matrix3(i + other.i, j + other.j, k + other.k)

    /** 演算子のオーバーロード Matrix3 - Matrix3 */
    operator fun minus(other: Matrix3) = Matrix3(i - other.i, j - other.j, k - other.k)

    /** 演算子のオーバーロード Matrix3 * Double */
    operator fun times(scalar: Double) = Matrix3(i * scalar, j * scalar, k * scalar)

    /** 演算子のオーバーロード Matrix3 * Vector3 */
    operator fun times(vec: Vector3): Vector3 {
        val (row1, row2, row3) = this.rows
        return Vector3(row1 dot vec, row2 dot vec, row3 dot vec)
    }

    /** 演算子のオーバーロード Matrix3 * Matrix3 */
    operator fun times(other: Matrix3): Matrix3 {
        val (row1, row2, row3) = this.rows
        return Matrix3(
            Vector3(row1 dot other.i, row1 dot other.j, row1 dot other.k),
            Vector3(row2 dot other.i, row2 dot other.j, row2 dot other.k),
            Vector3(row3 dot other.i, row3 dot other.j, row3 dot other.k)
        )
    }

    /** 行を取得 */
    private fun toRows() = listOf(Vector3(i.x, j.x, k.x), Vector3(i.y, j.y, k.y), Vector3(i.z, j.z, k.z))

    /** 転置行列の計算 */
    private fun transpose() = Matrix3(Vector3(i.x, j.x, k.x), Vector3(i.y, j.y, k.y), Vector3(i.z, j.z, k.z))

    /** 行列式の計算 */
    private fun computeDeterminant(): Double =
        i.x * j.y * k.z + j.x * k.y * i.z + k.x * i.y * j.z - k.x * j.y * i.z - i.x * k.y * j.z - j.x * i.y * k.z

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
    private fun checkOrthonormal(): Boolean {
        return i.isUnit && j.isUnit && k.isUnit
                && (i dot j).isClose(0.0) && (j dot k).isClose(0.0) && (k dot i).isClose(0.0)
    }

    /** ２つの行列が等しいことを判定する */
    fun isClose(other: Matrix3) = i.isClose(other.i) && j.isClose(other.j) && k.isClose(other.k)

    /** 表示用の文字列に変換 */
    override fun toString(): String = rows.joinToString("\n ", "[", "]")

    /**
     * 行列の生成に使う機能
     */
    companion object {
        val zero = Matrix3(Vector3.zero, Vector3.zero, Vector3.zero)
        val identity = Matrix3(Vector3.unitX, Vector3.unitY, Vector3.unitZ)
        fun random() = Matrix3(Vector3.random(), Vector3.random(), Vector3.random())

        /**
         * 要素から行列を作成する。
         */
        fun create(
            ix: Double, jx: Double, kx: Double,
            iy: Double, jy: Double, ky: Double,
            iz: Double, jz: Double, kz: Double
        ): Matrix3 {
            val i = Vector3(ix, iy, iz)
            val j = Vector3(jx, jy, jz)
            val k = Vector3(kx, ky, kz)
            return Matrix3(i, j, k)
        }

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
        fun createRotation(axis: Vector3, angle: Double): Matrix3? {
            if (axis.isClose(Vector3.zero)) {
                return null
            } else {
                val (x, y, z) = axis.unit
                val cos_ = cos(angle)
                val sin_ = sin(angle)
                return Matrix3.create(
                    cos_ + x * x * (1 - cos_), x * y * (1 - cos_) - z * sin_, z * x * (1 - cos_) + y * sin_,
                    x * y * (1 - cos_) + z * sin_, cos_ + y * y * (1 - cos_), y * z * (1 - cos_) - x * sin_,
                    z * x * (1 - cos_) - y * sin_, y * z * (1 - cos_) + x * sin_, cos_ + z * z * (1 - cos_)
                )
            }
        }
    }
}
