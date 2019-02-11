import kotlin.math.*
import kotlin.random.Random

/**
 * 3次元ベクトル
 * @property isUnit     単位ベクトルかどうかの判定
 * @property length     ベクトルの長さ
 * @property unit       単位ベクトル
 */
data class Vector3(val x: Double, val y: Double, val z: Double) {

   // プロパティ
    val isUnit: Boolean by lazy { (x * x + y * y + z * z).isClose(1.0) }
    val length: Double by lazy { sqrt(x * x + y * y + z * z) }
    val unit: Vector3 by lazy { this / length }

    // 演算子のオーバーロード
    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Double) = Vector3(scalar * x, scalar * y, scalar * z)
    operator fun div(k: Double) = Vector3(x / k, y / k, z / k)

    /** 内積 */
    infix fun dot(other: Vector3) = this.x * other.x + this.y * other.y + this.z * other.z

    /** クロス積 */
    infix fun cross(other: Vector3) =
        Vector3(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        )

    /** ２つのベクトルが等しいことを判定する */
    fun isClose(other: Vector3) = x.isClose(other.x) && y.isClose(other.y) && z.isClose(other.z)

    /** 別のベクトルに対する角度（ラジアン）を求める */
    fun getAngle(other: Vector3): Double = acos(this.unit dot other.unit)

    /** 別のベクトルに対する平行成分を求める */
    fun getParallel(other: Vector3) = other.unit * (this dot other.unit)

    /** 別のベクトルに対する垂直成分を求める */
    fun getPerpendicular(other: Vector3) = this - getParallel(other)

    /** リストに変換する */
    fun toList() = listOf(x, y, z)

    override fun toString(): String = "[%7.4f, %7.4f, %7.4f]".format(x, y, z)

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
        fun random() = Vector3(1 - 2 * Random.nextDouble(), 1 - 2 * Random.nextDouble(), 1 - 2 * Random.nextDouble())
    }
}
