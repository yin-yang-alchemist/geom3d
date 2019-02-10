import kotlin.math.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class Vector3Test {

    @org.junit.jupiter.api.Test
    fun getLength() {
        assertEquals(0.0, Vector3.zero.length)
        assertEquals(1.0, Vector3.unitX.length)
        assertEquals(1.0, Vector3.unitY.length)
        assertEquals(1.0, Vector3.unitZ.length)
        assertEquals(5.0, Vector3(3.0, 4.0, 0.0).length)
        for (count in 1..10) {
            val len = count.toDouble()
            val vec1 = Vector3(len * sqrt(1.0 / 3.0), len * sqrt(1.0 / 3.0), len * sqrt(1.0 / 3.0))
            assertTrue(vec1.length.isClose(len))
            val vec2 = Vector3(len * sqrt(1.0 / 2.0), len * sqrt(1.0 / 4.0), len * sqrt(1.0 / 4.0))
            assertTrue(vec2.length.isClose((len)))
        }
    }

    @org.junit.jupiter.api.Test
    fun getUnit() {
        assertEquals(Vector3.unitX, Vector3(2.0, 0.0, 0.0).unit)
        assertEquals(Vector3.unitY, Vector3(0.0, 3.0, 0.0).unit)
        assertEquals(Vector3.unitZ, Vector3(0.0, 0.0, 4.0).unit)
        for (i in 1..10) {
            val vec = Vector3.random()
            assertTrue(vec.unit.length.isClose(1.0))
        }
    }

    @org.junit.jupiter.api.Test
    fun dot() {
        assertEquals(1.0, Vector3.unitX dot Vector3.unitX)
        assertEquals(1.0, Vector3.unitY dot Vector3.unitY)
        assertEquals(1.0, Vector3.unitZ dot Vector3.unitZ)
        assertEquals(0.0, Vector3.unitX dot Vector3.unitY)
        assertEquals(0.0, Vector3.unitY dot Vector3.unitZ)
        assertEquals(0.0, Vector3.unitZ dot Vector3.unitX)
        for (count in 1..10) {
            val randomVec = Vector3.random()
            assertTrue(sqrt(randomVec dot randomVec).isClose(randomVec.length))
            assertTrue((randomVec dot randomVec.unit).isClose(randomVec.length))
        }
        // TODO: 内積の大きさをチェックする
    }

    @org.junit.jupiter.api.Test
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
        for (count in 1..10) {
            val i = Vector3.random().unit
            val j = Vector3.random().unit
            val k = (i cross j).unit
            val jOrtho = k cross i
            assertTrue((jOrtho cross k).isClose(i))
        }
        // TODO: 外積の長さをチェックする
    }
}