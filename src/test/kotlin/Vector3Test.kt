import kotlin.math.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.offset

internal class Vector3Test {

    /** 許容値 */
    private val TOL = 1e-8

    /** Vector3をJava配列に変換する（値を比較できるようにするため） */
    private fun Vector3.toArray() = doubleArrayOf(x, y, z)

    @Test
    @DisplayName("length: ゼロベクトルの長さが0")
    fun length_zero() {
        assertThat(Vector3.zero.length).isEqualTo(0.0, offset(TOL))
    }

    @Test
    @DisplayName("length: XYZ軸の単位ベクトルの長さが1")
    fun length_unit() {
        assertThat(Vector3.unitX.length).isEqualTo(1.0, offset(TOL))
        assertThat(Vector3.unitY.length).isEqualTo(1.0, offset(TOL))
        assertThat(Vector3.unitZ.length).isEqualTo(1.0, offset(TOL))
    }

    @ParameterizedTest
    @DisplayName("length: 長さの値が整数になるベクトル")
    @CsvSource(
        "3.0, 4.0, 0.0, 5.0",
        "2.0, 1.0, 2.0, 3.0",
        "4.0, 4.0, 7.0, 9.0",
        "5.0, 0.0, 12.0, 13.0"
    )
    fun length_parameterized(x: Double, y: Double, z: Double, expected: Double) {
        val vec = Vector3(x, y, z)
        assertThat(vec.length).isEqualTo(expected, offset(TOL))
    }

    @Test
    @DisplayName("length: 長さが1~100のベクトル")
    fun length_1to100() {
        for (count in 1..100) {
            val len = count.toDouble()
            val vec1 = Vector3(len * sqrt(1.0 / 3), len * sqrt(1.0 / 3), len * sqrt(1.0 / 3))
            assertThat(vec1.length).isCloseTo(len, offset(TOL))
            val vec2 = Vector3(len * sqrt(1.0 / 2), len * sqrt(1.0 / 4), len * sqrt(1.0 / 4))
            assertThat(vec2.length).isCloseTo(len, offset(TOL))
        }
    }

    @Test
    @DisplayName("unit: XYZ軸に沿ったベクトルの単位ベクトル")
    fun unit_xyz() {
        assertThat(Vector3(2.0, 0.0, 0.0).unit.toArray())
            .containsExactly(Vector3.unitX.toArray(), offset(TOL))
        assertThat(Vector3(0.0, 3.0, 0.0).unit.toArray())
            .containsExactly(Vector3.unitY.toArray(), offset(TOL))
        assertThat(Vector3(0.0, 0.0, 4.0).unit.toArray())
            .containsExactly(Vector3.unitZ.toArray(), offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("unit: ランダムなベクトルの単位ベクトルの長さが1")
    fun unit_random() {
        val vec = Vector3.random()
        assertThat(vec.unit.length).isEqualTo(1.0, offset(TOL))
    }

    @Test
    @DisplayName("dot: XYZ軸の単位ベクトルの自身との内積が1")
    fun dot_xyz1() {
        assertThat(Vector3.unitX dot Vector3.unitX).isEqualTo(1.0, offset(TOL))
        assertThat(Vector3.unitY dot Vector3.unitY).isEqualTo(1.0, offset(TOL))
        assertThat(Vector3.unitY dot Vector3.unitY).isEqualTo(1.0, offset(TOL))
    }

    @DisplayName("dot: XYZ軸の2つの単位ベクトルの内積が0")
    fun dot_xyz2() {
        assertThat(Vector3.unitX dot Vector3.unitY).isEqualTo(0.0, offset(TOL))
        assertThat(Vector3.unitY dot Vector3.unitZ).isEqualTo(0.0, offset(TOL))
        assertThat(Vector3.unitZ dot Vector3.unitX).isEqualTo(0.0, offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("dot: ランダムなベクトルの自信との内積が長さの2乗に等しい")
    fun dot_random1() {
        val vec = Vector3.random()
        assertThat(vec dot vec).isEqualTo(vec.length * vec.length, offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("dot: ランダムなベクトルと自身の単位ベクトルとの内積が長さに等しい")
    fun dot_random2() {
        val vec = Vector3.random()
        assertThat(vec dot vec.unit).isEqualTo(vec.length, offset(TOL))
    }

    @Test
    @DisplayName("cross: XYZ軸の単位ベクトルの自身との外積がゼロベクトル")
    fun cross_xyz1() {
        assertThat((Vector3.unitX cross Vector3.unitX).toArray())
            .containsExactly(Vector3.zero.toArray(), offset(TOL))
        assertThat((Vector3.unitY cross Vector3.unitY).toArray())
            .containsExactly(Vector3.zero.toArray(), offset(TOL))
        assertThat((Vector3.unitZ cross Vector3.unitZ).toArray())
            .containsExactly(Vector3.zero.toArray(), offset(TOL))
    }

    @Test
    @DisplayName("cross: XYZ軸の2つの単位ベクトルの外積が残り1つのベクトル")
    fun cross_xyz2() {
        assertThat((Vector3.unitX cross Vector3.unitY).toArray())
            .containsExactly(Vector3.unitZ.toArray(), offset(TOL))
        assertThat((Vector3.unitY cross Vector3.unitZ).toArray())
            .containsExactly(Vector3.unitX.toArray(), offset(TOL))
        assertThat((Vector3.unitZ cross Vector3.unitX).toArray())
            .containsExactly(Vector3.unitY.toArray(), offset(TOL))
        assertThat((Vector3.unitY cross Vector3.unitX).toArray())
            .containsExactly((-Vector3.unitZ).toArray(), offset(TOL))
        assertThat((Vector3.unitZ cross Vector3.unitY).toArray())
            .containsExactly((-Vector3.unitX).toArray(), offset(TOL))
        assertThat((Vector3.unitX cross Vector3.unitZ).toArray())
            .containsExactly((-Vector3.unitY).toArray(), offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("cross: ランダムなベクトル2つのクロス積からさらにクロス積を求めて元のベクトルに戻る")
    fun cross_random1() {
        val i = Vector3.random().unit
        val j = Vector3.random().unit
        val k = (i cross j).unit
        val jOrtho = k cross i
        assertThat(jOrtho dot i).isEqualTo(0.0, offset(TOL))
        assertThat((jOrtho cross k).toArray()).containsExactly(i.toArray(), offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("cross: ランダムなベクトル2つのクロス積の長さがそれぞれの長さの積にsin(θ)をかけた値")
    fun cross_random2() {
        val vec1 = Vector3.random()
        val vec2 = Vector3.random()
        val angle = vec1.getAngle(vec2)
        assertThat((vec1 cross vec2).length)
            .isEqualTo(vec1.length * vec2.length * sin(angle), offset(TOL))
    }

    @Test
    @DisplayName("getAngle: XYZ軸の単位ベクトルの自身とのなす角度が0")
    fun getAngle_xyz() {
        assertThat(Vector3.unitX.getAngle(Vector3.unitX)).isEqualTo(0.0, offset(TOL))
        assertThat(Vector3.unitY.getAngle(Vector3.unitY)).isEqualTo(0.0, offset(TOL))
        assertThat(Vector3.unitZ.getAngle(Vector3.unitZ)).isEqualTo(0.0, offset(TOL))
    }

    @Test
    @DisplayName("getAngle: XYZ軸の単位ベクトルの自身の逆ベクトルとのなす角度がπ")
    fun getAngle_xyz2() {
        assertThat(Vector3.unitX.getAngle(-Vector3.unitX)).isEqualTo(PI, offset(TOL))
        assertThat(Vector3.unitY.getAngle(-Vector3.unitY)).isEqualTo(PI, offset(TOL))
        assertThat(Vector3.unitZ.getAngle(-Vector3.unitZ)).isEqualTo(PI, offset(TOL))
    }

    @Test
    @DisplayName("getAngle: XYZ軸の2つの単位ベクトルのなす角度がπ/2")
    fun getAngle_xyz3() {
        assertThat(Vector3.unitX.getAngle(Vector3.unitY)).isEqualTo(PI / 2, offset(TOL))
        assertThat(Vector3.unitY.getAngle(Vector3.unitZ)).isEqualTo(PI / 2, offset(TOL))
        assertThat(Vector3.unitZ.getAngle(Vector3.unitX)).isEqualTo(PI / 2, offset(TOL))
        assertThat(Vector3.unitY.getAngle(Vector3.unitX)).isEqualTo(PI / 2, offset(TOL))
        assertThat(Vector3.unitZ.getAngle(Vector3.unitY)).isEqualTo(PI / 2, offset(TOL))
        assertThat(Vector3.unitX.getAngle(Vector3.unitZ)).isEqualTo(PI / 2, offset(TOL))

    }

    @RepeatedTest(100)
    @DisplayName("getAngle: ランダムなベクトル2つのなす角度から求めた内積の値が正しい")
    fun getAngle_random() {
        val vec1 = Vector3.random()
        val vec2 = Vector3.random()
        val actual = vec1.length * vec2.length * cos(vec1.getAngle(vec2))
        assertThat(actual).isEqualTo(vec1 dot vec2, offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("getParallel: ランダムなベクトル2つの平行成分による外積がゼロベクトル")
    fun getParallel_random() {
        val vec1 = Vector3.random()
        val vec2 = Vector3.random()
        assertThat((vec1.getParallel(vec2) cross vec2).toArray())
            .containsExactly(Vector3.zero.toArray(), offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("getPerpendicular: ランダムなベクトル2つの垂直成分による内積が0")
    fun getPerpendicular_random1() {
        val vec1 = Vector3.random()
        val vec2 = Vector3.random()
        assertThat(vec1.getPerpendicular(vec2) dot vec2).isEqualTo(0.0, offset(TOL))
    }

    @RepeatedTest(100)
    @DisplayName("getPerpendicular: ランダムなベクトル2つの外積が垂直成分による外積に等しい")
    fun getPerpendicular_random2() {
        val vec1 = Vector3.random()
        val vec2 = Vector3.random()
        assertThat((vec1.getPerpendicular(vec2) cross vec2).toArray())
            .containsExactly((vec1 cross vec2).toArray(), offset(TOL))
    }
}