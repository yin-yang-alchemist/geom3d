import kotlin.math.*

/**
 * 4x4の同次変換行列
 * @property rot    回転行列
 * @property trans  並進ベクトル
 * @property cols   4つの列を表すリスト
 * @property rows   4つの行を表すリスト
 * @property inv    逆行列
 */
data class Matrix4(val rot: Matrix3, val trans: Vector3) {

    // プロパティ

    val cols: List<List<Double>> by lazy { toCols() }
    val rows: List<List<Double>> by lazy { toRows() }
    val inv: Matrix4? by lazy { computeInverse() }

    /** 演算子のオーバーロード Matrix4 * Matrix4 */
    operator fun times(other: Matrix4) =
        Matrix4(this.rot * other.rot, this.rot * other.trans + this.trans)

    /** 行を取得 */
    fun toCols(): List<List<Double>> =
        listOf(
            rot.cols[0].toList() + 0.0,
            rot.cols[1].toList() + 0.0,
            rot.cols[2].toList() + 0.0,
            trans.toList() + 1.0
        )

    /** 列を取得 */
    fun toRows(): List<List<Double>> =
        listOf(
            rot.rows[0].toList() + trans.x,
            rot.rows[1].toList() + trans.y,
            rot.rows[2].toList() + trans.z,
            listOf(0.0, 0.0, 0.0, 1.0)
        )

    /** 逆行列の計算 */
    fun computeInverse() = when {
        rot.inv == null -> null
        else -> Matrix4(rot.inv!!, -(rot.inv!! * trans))
    }

    /** ベクトルに変換を適用する */
    fun transform(vec: Vector3): Vector3 = rot * vec + trans

    /** 表示用の文字列に変換 */
    override fun toString(): String =
        toRows()
            .map { "[%7.4f, %7.4f, %7.4f, %7.4f]".format(it[0], it[1], it[2], it[3]) }
            .joinToString("\n ", "[", "]")

    /**
     * 行列の生成に使う機能
     */
    companion object {
        val identity = Matrix4(Matrix3.identity, Vector3.zero)

        /** ランダムな行列を作成する */
        fun random() = Matrix4(Matrix3.random(), Vector3.random())

        /**
         * 平行移動を表す行列を作成する。
         */
        fun createTranslation(trans: Vector3) = Matrix4(Matrix3.identity, trans)

        /**
         * スケーリングを表す行列を作成する。
         */
        fun createScale(axis: Vector3, scale: Double, center: Vector3 = Vector3.zero): Matrix4 {
            val scaling = Matrix3.createScale(axis, scale)
            return Matrix4(scaling, center - scaling * center)
        }

        /**
         * 平面上への投影を表す行列を作成する。
         */
        fun createProjection(axis: Vector3, center: Vector3 = Vector3.zero) = createScale(axis, 0.0, center)

        /**
         * ミラーリングを表す行列を作成する。
         */
        fun createMirror(axis: Vector3, center: Vector3 = Vector3.zero) = createScale(axis, -1.0, center)

        /**
         * TODO: 原点を持つ座標系を表す行列を作成する。
         */

        /**
         * 回転を表す行列を作成する。
         */
        fun createRotation(axis: Vector3, angle: Double, center: Vector3 = Vector3.zero): Matrix4? {
            val rot = Matrix3.createRotation(axis, angle)
            return when (rot) {
                null -> null
                else -> Matrix4(rot, center - rot * center)
            }
        }

        /**
         * TODO: 座標系から座標系へ移動する行列を作成する。
         */
    }
}