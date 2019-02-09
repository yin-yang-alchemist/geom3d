import com.sun.xml.internal.ws.policy.AssertionSet
import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertEquals
import kotlin.test.asserter

internal class Vector3Test {

    @org.junit.jupiter.api.Test
    fun getLength() {
        assertEquals(0.0, Vector3.zero.length)
        assertEquals(1.0, Vector3.unitX.length)
        assertEquals(1.0, Vector3.unitY.length)
        assertEquals(1.0, Vector3.unitZ.length)
        assertEquals(5.0, Vector3(3.0, 4.0, 0.0).length)
    }

    @org.junit.jupiter.api.Test
    fun normalize() {
        assertEquals(Vector3.unitX, Vector3(2.0, 0.0, 0.0).normalize())
        assertEquals(Vector3.unitY, Vector3(0.0, 3.0, 0.0).normalize())
        assertEquals(Vector3.unitZ, Vector3(0.0, 0.0, 4.0).normalize())
        assertEquals(1.0, Vector3.random().normalize().length)
    }

    @org.junit.jupiter.api.Test
    operator fun unaryMinus() {
        assertEquals(Vector3.zero, -Vector3.zero)
        val randomVec = Vector3.random()
        assertEquals(randomVec * (-1.0), -randomVec)
        assertEquals(randomVec, -(-randomVec))
        assertEquals(Vector3.zero, -randomVec + randomVec)
    }

    @org.junit.jupiter.api.Test
    fun plus() {
        assertEquals(Vector3.unitX, Vector3.zero + Vector3.unitX)
        assertEquals(Vector3(0.0, 2.0, 0.0), Vector3.unitY + Vector3.unitY)
        assertEquals(Vector3(1.0, 1.0, 1.0), Vector3.unitX + Vector3.unitY + Vector3.unitZ)
    }

    @org.junit.jupiter.api.Test
    fun minus() {
        assertEquals(-Vector3.unitX, Vector3.zero - Vector3.unitX)
        assertEquals(Vector3.zero, Vector3.unitY - Vector3.unitY)
        assertEquals(Vector3(1.0, 0.0, -1.0), Vector3.unitX - Vector3.unitZ)
    }

    @org.junit.jupiter.api.Test
    fun times() {
        assertEquals(Vector3.zero, Vector3.zero * 3.0)
        assertEquals(Vector3(5.0, 0.0, 0.0), Vector3.unitX * 5.0)
        assertEquals(Vector3(0.0, 4.0, 0.0), Vector3.unitY * 4.0)
        assertEquals(Vector3(0.0, 0.0, 3.0), Vector3.unitZ * 3.0)
    }

    @org.junit.jupiter.api.Test
    fun div() {
        throw NotImplementedError()
    }

    @org.junit.jupiter.api.Test
    fun dot() {
        throw NotImplementedError()
    }

    @org.junit.jupiter.api.Test
    fun cross() {
        throw NotImplementedError()
    }
}