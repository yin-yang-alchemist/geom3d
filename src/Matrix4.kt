fun main() {
    val a = Matrix4.identity
    println(a)
}

/**
 * 4x4の同次変換行列
 * @property rot    回転行列
 * @property trans  並進ベクトル
 */
data class Matrix4(val rot: Matrix3, val trans: Vector3) {

    /**
     * 行列の生成に使う機能
     */
    companion object {
        val zero = Matrix4(Matrix3.zero, Vector3.zero)
        val identity = Matrix4(Matrix3.identity, Vector3.zero)
    }

    // TODO: 各行を表すプロパティ

    // TODO: 各列を表すプロパティ

    // TODO: Quaternionへの変換

    fun toList(): List<List<Double>> =
        listOf(
            rot.i.toList() + trans.x,
            rot.j.toList() + trans.y,
            rot.k.toList() + trans.z,
            listOf(0.0, 0.0, 0.0, 1.0)
        )

    override fun toString(): String =
        toList()
            .map { "[%7.4f, %7.4f, %7.4f, %7.4f]".format(it[0], it[1], it[2], it[3]) }
            .joinToString("\n ", "[", "]")

}