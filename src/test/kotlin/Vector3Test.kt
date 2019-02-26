import kotlin.math.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class Vector3Test {

    /** 許容値 */
    private val TOL = 1e-9

    /** Vector3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Vector3.toArray() = doubleArrayOf(x, y, z)

    @Test
    fun getLength() {
        assertEquals(0.0, Vector3.zero.length, TOL)
        assertEquals(1.0, Vector3.unitX.length, TOL)
        assertEquals(1.0, Vector3.unitY.length, TOL)
        assertEquals(1.0, Vector3.unitZ.length, TOL)
        assertEquals(5.0, Vector3(3.0, 4.0, 0.0).length, TOL)
        for (count in 1..100) {
            val len = count.toDouble()
            val vec1 = Vector3(len * sqrt(1.0 / 3), len * sqrt(1.0 / 3), len * sqrt(1.0 / 3))
            assertEquals(len, vec1.length, TOL)
            val vec2 = Vector3(len * sqrt(1.0 / 2), len * sqrt(1.0 / 4), len * sqrt(1.0 / 4))
            assertEquals(len, vec2.length, TOL)
        }
    }

    @Test
    fun getUnit() {
        assertArrayEquals(Vector3.unitX.toArray(), Vector3(2.0, 0.0, 0.0).unit.toArray(), TOL)
        assertArrayEquals(Vector3.unitY.toArray(), Vector3(0.0, 3.0, 0.0).unit.toArray(), TOL)
        assertArrayEquals(Vector3.unitZ.toArray(), Vector3(0.0, 0.0, 4.0).unit.toArray(), TOL)
        for (i in 1..100) {
            val vec = Vector3.random()
            assertEquals(1.0, vec.unit.length, TOL)
        }
    }

    @Test
    fun dot() {
        assertEquals(1.0, Vector3.unitX dot Vector3.unitX, TOL)
        assertEquals(1.0, Vector3.unitY dot Vector3.unitY, TOL)
        assertEquals(1.0, Vector3.unitZ dot Vector3.unitZ, TOL)
        assertEquals(0.0, Vector3.unitX dot Vector3.unitY, TOL)
        assertEquals(0.0, Vector3.unitY dot Vector3.unitZ, TOL)
        assertEquals(0.0, Vector3.unitZ dot Vector3.unitX, TOL)
        for (count in 1..100) {
            val vec = Vector3.random()
            assertEquals(vec.length, sqrt(vec dot vec), TOL)
            assertEquals(vec.length, (vec dot vec.unit), TOL)
        }
    }

    @Test
    fun cross() {
        assertArrayEquals(Vector3.zero.toArray(), (Vector3.unitX cross Vector3.unitX).toArray(), TOL)
        assertArrayEquals(Vector3.zero.toArray(), (Vector3.unitY cross Vector3.unitY).toArray(), TOL)
        assertArrayEquals(Vector3.zero.toArray(), (Vector3.unitZ cross Vector3.unitZ).toArray(), TOL)
        assertArrayEquals(Vector3.unitZ.toArray(), (Vector3.unitX cross Vector3.unitY).toArray(), TOL)
        assertArrayEquals(Vector3.unitX.toArray(), (Vector3.unitY cross Vector3.unitZ).toArray(), TOL)
        assertArrayEquals(Vector3.unitY.toArray(), (Vector3.unitZ cross Vector3.unitX).toArray(), TOL)
        assertArrayEquals((-Vector3.unitZ).toArray(), (Vector3.unitY cross Vector3.unitX).toArray(), TOL)
        assertArrayEquals((-Vector3.unitX).toArray(), (Vector3.unitZ cross Vector3.unitY).toArray(), TOL)
        assertArrayEquals((-Vector3.unitY).toArray(), (Vector3.unitX cross Vector3.unitZ).toArray(), TOL)
        // クロス積からさらにクロス積を求めて元のベクトルに戻ることをチェックする
        for (count in 1..100) {
            val i = Vector3.random().unit
            val j = Vector3.random().unit
            val k = (i cross j).unit
            val jOrtho = k cross i
            assertEquals(0.0, (jOrtho dot i), TOL)
            assertArrayEquals(i.toArray(), (jOrtho cross k).toArray(), TOL)
        }
        // クロス積の長さをチェックする
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            val angle = vec1.getAngle(vec2)
            assertEquals(
                vec1.length * vec2.length * sin(angle),
                (vec1 cross vec2).length,
                TOL
            )
        }
    }

    @Test
    fun getAngle() {
        assertEquals(0.0, Vector3.unitX.getAngle(Vector3.unitX), TOL)
        assertEquals(PI, Vector3.unitX.getAngle(-Vector3.unitX), TOL)
        assertEquals(PI / 2, Vector3.unitX.getAngle(Vector3.unitY), TOL)
        assertEquals(PI / 2, Vector3.unitY.getAngle(Vector3.unitZ), TOL)
        assertEquals(PI / 2, Vector3.unitZ.getAngle(Vector3.unitX), TOL)
        assertEquals(PI / 2, Vector3.unitY.getAngle(Vector3.unitX), TOL)
        assertEquals(PI / 2, Vector3.unitZ.getAngle(Vector3.unitY), TOL)
        assertEquals(PI / 2, Vector3.unitX.getAngle(Vector3.unitZ), TOL)
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            assertEquals(
                vec1 dot vec2,
                vec1.length * vec2.length * cos(vec1.getAngle(vec2)),
                TOL
            )
        }
    }

    @Test
    fun getParallel() {
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            assertArrayEquals(
                Vector3.zero.toArray(),
                (vec1.getParallel(vec2) cross vec2).toArray(),
                TOL
            )
        }
    }

    @Test
    fun getPerpendicular() {
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            assertEquals(0.0, vec1.getPerpendicular(vec2) dot vec2, TOL)
            assertArrayEquals(
                (vec1 cross vec2).toArray(),
                (vec1.getPerpendicular(vec2) cross vec2).toArray(),
                TOL
            )
        }
    }
}