fun main() {
    val a = Quaternion(1.0, 2.0, 3.0, 4.0)
    println(a)
}

/**
 * クォータニオン
 */
data class Quaternion(val x: Double, val y: Double, val z: Double, val w: Double) {
    constructor(vec: Vector3, w: Double) : this(vec.x, vec.y, vec.z, w)

    operator fun plus(other: Quaternion) =
        Quaternion(x + other.x, y + other.y, z + other.z, w + other.w)

    operator fun times(k: Double) =
        Quaternion(k * x, k * y, k * z, k * w)

    operator fun times(other: Quaternion): Quaternion {
        val vec1 = Vector3(this.x, this.y, this.z)
        val vec2 = Vector3(other.x, other.y, other.z)
        return Quaternion(
            vec1 cross vec2 + vec1 * other.w + vec2 * this.w,
            this.w * other.w - (vec1 dot vec2)
        )
    }

    fun inverse() = Quaternion(-x, -y, -z, w)
}
