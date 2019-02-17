import kotlin.math.*

/**
 * ２つの浮動小数点数が等しいことを判定する。
 * 判定基準はnumpy.iscloseの定義に合わせる。
 * [numpy.isclose — NumPy v1.16 Manual](https://docs.scipy.org/doc/numpy-1.16.1/reference/generated/numpy.isclose.html?highlight=isclose#numpy.isclose)
 */
fun Double.isClose(other: Double, rtol: Double = 1e-5, atol: Double = 1e-8): Boolean {
    return abs(this - other) <= (atol + rtol * abs(other))
}

/**
 * 浮動小数点数を表示用の文字列に変換する。
 */
fun Double.format(length: Int, precision: Int) =
    ("%" + length + "." + precision + "f").format(this).trimEnd('0')
