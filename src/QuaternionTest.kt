import kotlin.math.*
import kotlin.random.Random
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class QuaternionTest {

    @Test
    fun getNorm() {
        assertEquals(0.0, Quaternion(Vector3.zero, 0.0).norm)
        assertEquals(1.0, Quaternion(Vector3.unitX, 0.0).norm)
        assertEquals(1.0, Quaternion(Vector3.unitY, 0.0).norm)
        assertEquals(1.0, Quaternion(Vector3.unitZ, 0.0).norm)
        assertEquals(6.0, Quaternion(3.0, 3.0, 3.0, 3.0).norm)
    }

    @Test
    fun getUnit() {
        // 単位クォータニオンのノルムが１であることをチェックする。
        for (count in 1..100) {
            val quat = Quaternion(Vector3.random(), Random.nextDouble())
            assertTrue(quat.unit.norm.isClose(1.0))
        }
    }

    @Test
    fun getInv() {
        // 逆元をかけると(0, 0, 0, 1)になることをチェックする。
        for (count in 1..100) {
            val quat = Quaternion(Vector3.random(), Random.nextDouble())
            assertTrue((quat * quat.inv).isClose(Quaternion.identity))
        }
    }

    @Test
    fun getAngle() {
        // 回転角度がクォータニオンを作成時に指定した回転角度と等しいことをチェックする。
        for (count in 1..100) {
            val angle = Random.nextDouble()
            val quat = Quaternion.createRotation(Vector3.random(), angle)
            if (quat != null) assertTrue(quat.angle.isClose(angle))
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
            assertTrue((quat1 * quat2).norm.isClose(quat1.norm * quat2.norm))
        }
        // 同じ回転軸をもつ２つのクォータニオンの積の回転角度が、２つの回転角度の和になっていることをチェックする。
        // ２つの角度の和がπを超えないことが前提
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle1 = Random.nextDouble()
            val angle2 = Random.nextDouble()
            val quat1 = Quaternion.createRotation(axis, angle1)
            val quat2 = Quaternion.createRotation(axis, angle2)
            if (quat1 != null && quat2 != null) {
                assertTrue((quat1 * quat2).angle.isClose(angle1 + angle2))
            }
        }
    }

    @Test
    fun toMatrix() {
        // 各軸周り60度回転の回転行列が正しいことをチェックする。
        val quat_x60deg = Quaternion.createRotation(Vector3.unitX, PI / 3)!!
        val mat_x60deg = Matrix3.createRotation(Vector3.unitX, PI / 3)!!
        assertTrue(quat_x60deg.toMatrix().isClose(mat_x60deg))
        val quat_y60deg = Quaternion.createRotation(Vector3.unitY, PI / 3)!!
        val mat_y60deg = Matrix3.createRotation(Vector3.unitY, PI / 3)!!
        assertTrue(quat_y60deg.toMatrix().isClose(mat_y60deg))
        val quat_z60deg = Quaternion.createRotation(Vector3.unitZ, PI / 3)!!
        val mat_z60deg = Matrix3.createRotation(Vector3.unitZ, PI / 3)!!
        assertTrue(quat_z60deg.toMatrix().isClose(mat_z60deg))
        // 直接作成した回転行列とクォータニオンから作成した回転行列が等しいことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle = Random.nextDouble()
            val mat = Matrix3.createRotation(axis, angle)
            val quat = Quaternion.createRotation(axis, angle)
            if (quat != null && mat != null) {
                assertTrue(quat.toMatrix().isClose(mat))
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
            assertTrue(quat.norm.isClose(1.0))
        }
    }

    @Test
    fun createRotation() {
        // 回転角度０で作成したクォータニオンが(0, 0, 0, 1)であることをチェックする。
        for (count in 1..100) {
            val quat = Quaternion.createRotation(Vector3.random(), 0.0)
            if (quat != null) assertTrue(quat.isClose(Quaternion.identity))
        }
        // 回転軸・回転角度がクォータニオンを作成時に指定した回転軸・回転角度と等しいことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle = Random.nextDouble()
            val quat = Quaternion.createRotation(axis, angle)
            if (quat != null) {
                assertTrue(quat.angle.isClose(angle))
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
            assertTrue(quat.toMatrix().isClose(mat))
        }
        // 回転行列から変換したクォータニオンの回転軸・回転角度が正しいことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val angle = Random.nextDouble()
            val mat = Matrix3.createRotation(axis, angle)
            if (mat != null) {
                val quat = Quaternion.createFromMatrix(mat)
                if (quat != null) {
                    assertTrue(quat.axis.isClose(axis.unit))
                    assertTrue(quat.angle.isClose(angle))
                }
            }
        }
    }
}