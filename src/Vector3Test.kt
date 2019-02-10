import kotlin.math.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class Vector3Test {

    @Test
    fun getLength() {
        assertEquals(0.0, Vector3.zero.length)
        assertEquals(1.0, Vector3.unitX.length)
        assertEquals(1.0, Vector3.unitY.length)
        assertEquals(1.0, Vector3.unitZ.length)
        assertEquals(5.0, Vector3(3.0, 4.0, 0.0).length)
        for (count in 1..100) {
            val len = count.toDouble()
            val vec1 = Vector3(len * sqrt(1.0 / 3.0), len * sqrt(1.0 / 3.0), len * sqrt(1.0 / 3.0))
            assertTrue(vec1.length.isClose(len))
            val vec2 = Vector3(len * sqrt(1.0 / 2.0), len * sqrt(1.0 / 4.0), len * sqrt(1.0 / 4.0))
            assertTrue(vec2.length.isClose((len)))
        }
    }

    @Test
    fun getUnit() {
        assertEquals(Vector3.unitX, Vector3(2.0, 0.0, 0.0).unit)
        assertEquals(Vector3.unitY, Vector3(0.0, 3.0, 0.0).unit)
        assertEquals(Vector3.unitZ, Vector3(0.0, 0.0, 4.0).unit)
        for (i in 1..100) {
            val vec = Vector3.random()
            assertTrue(vec.unit.length.isClose(1.0))
        }
    }

    @Test
    fun dot() {
        assertEquals(1.0, Vector3.unitX dot Vector3.unitX)
        assertEquals(1.0, Vector3.unitY dot Vector3.unitY)
        assertEquals(1.0, Vector3.unitZ dot Vector3.unitZ)
        assertEquals(0.0, Vector3.unitX dot Vector3.unitY)
        assertEquals(0.0, Vector3.unitY dot Vector3.unitZ)
        assertEquals(0.0, Vector3.unitZ dot Vector3.unitX)
        for (count in 1..100) {
            val randomVec = Vector3.random()
            assertTrue(sqrt(randomVec dot randomVec).isClose(randomVec.length))
            assertTrue((randomVec dot randomVec.unit).isClose(randomVec.length))
        }
        // TODO: 内積の大きさをチェックする
    }

    @Test
    fun cross() {
        assertEquals(Vector3.zero, Vector3.unitX cross Vector3.unitX)
        assertEquals(Vector3.zero, Vector3.unitY cross Vector3.unitY)
        assertEquals(Vector3.zero, Vector3.unitZ cross Vector3.unitZ)
        assertEquals(Vector3.unitZ, Vector3.unitX cross Vector3.unitY)
        assertEquals(Vector3.unitX, Vector3.unitY cross Vector3.unitZ)
        assertEquals(Vector3.unitY, Vector3.unitZ cross Vector3.unitX)
        assertEquals(-Vector3.unitZ, Vector3.unitY cross Vector3.unitX)
        assertEquals(-Vector3.unitX, Vector3.unitZ cross Vector3.unitY)
        assertEquals(-Vector3.unitY, Vector3.unitX cross Vector3.unitZ)
        // クロス積からさらにクロス積を求めて元のベクトルに戻ることをチェックする
        for (count in 1..100) {
            val i = Vector3.random().unit
            val j = Vector3.random().unit
            val k = (i cross j).unit
            val jOrtho = k cross i
            assertTrue((jOrtho dot i).isClose(0.0))
            assertTrue((jOrtho cross k).isClose(i))
        }
        // クロス積の長さをチェックする
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            val angle = vec1.getAngle(vec2)
            assertTrue((vec1 cross vec2).length.isClose(vec1.length * vec2.length * sin(angle)))
        }
    }

    @Test
    fun getAngle() {
        assertEquals(0.0, Vector3.unitX.getAngle(Vector3.unitX))
        assertEquals(PI, Vector3.unitX.getAngle(-Vector3.unitX))
        assertEquals(PI / 2, Vector3.unitX.getAngle(Vector3.unitY))
        assertEquals(PI / 2, Vector3.unitY.getAngle(Vector3.unitZ))
        assertEquals(PI / 2, Vector3.unitZ.getAngle(Vector3.unitX))
        assertEquals(PI / 2, Vector3.unitY.getAngle(Vector3.unitX))
        assertEquals(PI / 2, Vector3.unitZ.getAngle(Vector3.unitY))
        assertEquals(PI / 2, Vector3.unitX.getAngle(Vector3.unitZ))
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            assertTrue((vec1.length * vec2.length * cos(vec1.getAngle(vec2))).isClose(vec1 dot vec2))
        }
    }

    @Test
    fun getParallel() {
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            assertTrue((vec1.getParallel(vec2) cross vec2).isClose(Vector3.zero))
        }
    }

    @Test
    fun getPerpendicular() {
        for (count in 1..100) {
            val vec1 = Vector3.random()
            val vec2 = Vector3.random()
            assertTrue((vec1.getPerpendicular(vec2) dot vec2).isClose(0.0))
            assertTrue((vec1.getPerpendicular(vec2) cross vec2).isClose(vec1 cross vec2))
        }
    }
}