import kotlin.math.sqrt

fun main() {
    val vec = Vector3(1.0, 2.0, 3.0)
    println(vec)
    println(Vector3.unitZ cross Vector3.unitX)
}

/**
 * 3次元ベクトル
 */
data class Vector3(val x: Double, val y: Double, val z: Double) {

    /**
     * @property zero   ゼロベクトル
     * @property unitX  X方向の単位ベクトル
     * @property unitY  Y方向の単位ベクトル
     * @property unitZ  Z方向の単位ベクトル
     */
    companion object {
        val zero = Vector3(0.0, 0.0, 0.0)
        val unitX = Vector3(1.0, 0.0, 0.0)
        val unitY = Vector3(0.0, 1.0, 0.0)
        val unitZ = Vector3(0.0, 0.0, 1.0)
    }

    val length: Double = sqrt(x * x + y * y + z * z)

    fun normalize() = this / this.length

    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)

    operator fun times(k: Double) = Vector3(k * x, k * y, k * z)

    operator fun div(k: Double) = Vector3(x / k, y / k, z / k)

    infix fun dot(other: Vector3) = x * other.x + y * other.y + z * other.z

    infix fun cross(other: Vector3) =
        Vector3(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        )

    fun toList() = listOf(x, y, z)

    override fun toString(): String = "[%7.4f, %7.4f, %7.4f]".format(x, y, z)
}
