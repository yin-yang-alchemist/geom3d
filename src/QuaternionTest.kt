import kotlin.math.*
import kotlin.random.Random
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class QuaternionTest {

    /** 許容値 */
    private val TOL = 1e-9

    /** Vector3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Vector3.toArray() = doubleArrayOf(x, y, z)

    /** Matrix3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Matrix3.toArray() = doubleArrayOf(i.x, j.x, k.x, i.y, j.y, k.y, i.z, j.z, k.z)

    /** QuaternionをJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Quaternion.toArray() = doubleArrayOf(x, y, z, w)

    @Test
    fun getNorm() {
        assertEquals(0.0, Quaternion(Vector3.zero, 0.0).norm, TOL)
        assertEquals(1.0, Quaternion(Vector3.unitX, 0.0).norm, TOL)
        assertEquals(1.0, Quaternion(Vector3.unitY, 0.0).norm, TOL)
        assertEquals(1.0, Quaternion(Vector3.unitZ, 0.0).norm, TOL)
        assertEquals(6.0, Quaternion(3.0, 3.0, 3.0, 3.0).norm, TOL)
    }

    @Test
    fun getUnit() {
        // 単位クォータニオンのノルムが１であることをチェックする。
        for (count in 1..100) {
            val quat = Quaternion(Vector3.random(), Random.nextDouble())
            assertEquals(1.0, quat.unit.norm, TOL)
        }
    }

    @Test
    fun getInv() {
        // 逆元をかけると(0, 0, 0, 1)になることをチェックする。
        for (count in 1..100) {
            val quat = Quaternion(Vector3.random(), Random.nextDouble())
            assertArrayEquals(Quaternion.identity.toArray(), (quat * quat.inv).toArray(), TOL)
        }
    }

    @Test
    fun getAngle() {
        // 回転角度がクォータニオンを作成時に指定した回転角度と等しいことをチェックする。
        for (count in 1..100) {
            val angle = Random.nextDouble()
            val quat = Quaternion.createRotation(Vector3.random(), angle)
            if (quat != null) assertEquals(angle, quat.angle, TOL)
        }
    }

    @Test
    fun getAxis() {
        // 回転軸がクォータニオンを作成時に指定した回転軸と等しいことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val quat = Quaternion.createRotation(axis, Random.nextDouble())
            if (quat != null) {
                assertTrue(quat.axis.isClose(axis.unit) || quat.axis.isClose(-axis.unit))
            }
        }
    }

    @Test
    fun times() {
        // 積のノルムが２つのノルムの積になっていることをチェックする。
        for (count in 1..100) {
            val quat1 = Quaternion(Vector3.random(), Random.nextDouble())
            val quat2 = Quaternion(Vector3.random(), Random.nextDouble())
            assertEquals(quat1.norm * quat2.norm, (quat1 * quat2).norm, TOL)
        }
        // 同じ回転軸をもつ２つのクォータニオンの積の回転角度が、２つの回転角度の和になっていることをチェックする。
        // ２つの角度の和がーπ〜πの範囲にあることが前提
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle1 = Random.nextDouble()
            val angle2 = Random.nextDouble()
            val quat1 = Quaternion.createRotation(axis, angle1)
            val quat2 = Quaternion.createRotation(axis, angle2)
            if (quat1 != null && quat2 != null) {
                assertEquals(angle1 + angle2, (quat1 * quat2).angle, TOL)
            }
        }
    }

    @Test
    fun toMatrix() {
        // 各軸周り60度回転の回転行列が正しいことをチェックする。
        val quat_x60deg = Quaternion.createRotation(Vector3.unitX, PI / 3)!!
        val mat_x60deg = Matrix3.createRotation(Vector3.unitX, PI / 3)!!
        assertArrayEquals(mat_x60deg.toArray(), quat_x60deg.toMatrix().toArray(), TOL)
        val quat_y60deg = Quaternion.createRotation(Vector3.unitY, PI / 3)!!
        val mat_y60deg = Matrix3.createRotation(Vector3.unitY, PI / 3)!!
        assertArrayEquals(mat_y60deg.toArray(), quat_y60deg.toMatrix().toArray(), TOL)
        val quat_z60deg = Quaternion.createRotation(Vector3.unitZ, PI / 3)!!
        val mat_z60deg = Matrix3.createRotation(Vector3.unitZ, PI / 3)!!
        assertArrayEquals(mat_z60deg.toArray(), quat_z60deg.toMatrix().toArray(), TOL)
        // 直接作成した回転行列とクォータニオンから作成した回転行列が等しいことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle = Random.nextDouble()
            val mat = Matrix3.createRotation(axis, angle)
            val quat = Quaternion.createRotation(axis, angle)
            if (quat != null && mat != null) {
                assertArrayEquals(mat.toArray(), quat.toMatrix().toArray(), TOL)
            }
        }
    }

    @Test
    fun createUnit() {
        // 作成したクォータニオンのノルムが１であることをチェックする。
        for (count in 1..100) {
            val x = Random.nextDouble()
            val y = Random.nextDouble()
            val z = Random.nextDouble()
            val w = Random.nextDouble()
            val quat = Quaternion.createUnit(x, y, z, w)
            assertEquals(1.0, quat.norm, TOL)
        }
    }

    @Test
    fun createRotation() {
        // 回転角度０で作成したクォータニオンが(0, 0, 0, 1)であることをチェックする。
        for (count in 1..100) {
            val quat = Quaternion.createRotation(Vector3.random(), 0.0)
            if (quat != null) assertArrayEquals(Quaternion.identity.toArray(), quat.toArray(), TOL)
        }
        // 回転軸・回転角度がクォータニオンを作成時に指定した回転軸・回転角度と等しいことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle = Random.nextDouble()
            val quat = Quaternion.createRotation(axis, angle)
            if (quat != null) {
                assertEquals(angle, quat.angle, TOL)
                assertTrue(quat.axis.isClose(axis.unit) || quat.axis.isClose(-axis.unit))
            }
        }
    }

    @Test
    fun createFromMatrix() {
        // 行列からクォータニオンを作成し行列に戻すと元の行列と等しいことをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.createCsys(Vector3.random(), Vector3.random())
            val quat = Quaternion.createFromMatrix(mat)!!
            assertArrayEquals(mat.toArray(), quat.toMatrix().toArray(), TOL)
        }
        // 回転行列から変換したクォータニオンの回転軸・回転角度が正しいことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle = Random.nextDouble()
            val mat = Matrix3.createRotation(axis, angle)
            if (mat != null) {
                val quat = Quaternion.createFromMatrix(mat)
                if (quat != null) {
                    assertArrayEquals(axis.unit.toArray(), quat.axis.toArray(), TOL)
                    assertEquals(angle, quat.angle, TOL)
                }
            }
        }
    }
}