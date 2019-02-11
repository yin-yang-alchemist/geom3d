import kotlin.math.*
import kotlin.random.Random
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class Matrix3Test {

    /** 許容値 */
    private val TOL = 1e-9

    /** Vector3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Vector3.toArray() = doubleArrayOf(x, y, z)

    /** Matrix3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Matrix3.toArray() = doubleArrayOf(i.x, j.x, k.x, i.y, j.y, k.y, i.z, j.z, k.z)

    @Test
    fun getDet() {
        // 単位行列の定数倍の行列式
        for (count in 1..100) {
            val k = Random.nextDouble()
            assertEquals(k.pow(3), (Matrix3.identity * k).det, TOL)
        }
        // 対角行列の行列式
        for (count in 1..100) {
            val i = Random.nextDouble()
            val j = Random.nextDouble()
            val k = Random.nextDouble()
            assertEquals(i * j * k, Matrix3.createDiag(i, j, k).det, TOL)
        }
        // 転置・逆行列の行列式
        for (count in 1..100) {
            val mat = Matrix3.random()
            assertEquals(mat.det, mat.T.det, TOL)
        }
        // 積の行列式
        for (count in 1..100) {
            val mat1 = Matrix3.random()
            val mat2 = Matrix3.random()
            assertEquals(mat1.det * mat2.det, (mat1 * mat2).det, TOL)
        }
    }

    @Test
    fun getT() {
        for (count in 1..100) {
            val mat = Matrix3.random()
            assertArrayEquals(mat.toArray(), mat.T.T.toArray(), TOL)
        }
    }

    @Test
    fun getInv() {
        // 行列に逆行列をかけると単位行列になることをチェックする
        for (count in 1..100) {
            val mat = Matrix3(Vector3.random(), Vector3.random(), Vector3.random())
            if (mat.inv != null) {
                assertArrayEquals(Matrix3.identity.toArray(), (mat * mat.inv!!).toArray(), TOL)
            }
        }
    }

    @Test
    fun times_Vector3() {
        // 単位行列の定数倍をかける
        for (count in 1..100) {
            val vec = Vector3.random()
            val k = Random.nextDouble()
            assertArrayEquals(vec.toArray(), (Matrix3.identity * vec).toArray(), TOL)
            assertArrayEquals((vec * k).toArray(), ((Matrix3.identity * k) * vec).toArray(), TOL)
        }
        // 行列をかけてさらに逆行列をかけた時に元のベクトルに戻ることをチェックする
        for (count in 1..100) {
            val vec = Vector3.random()
            val mat = Matrix3.random()
            assertArrayEquals(vec.toArray(), (mat.inv!! * (mat * vec)).toArray(), TOL)
        }
        // 回転行列をかけて長さが変わらないことをチェックする
        for (count in 1..100) {
            val vec = Vector3.random()
            val mat = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            if (mat != null) assertEquals(vec.length, (mat * vec).length, TOL)
        }
    }

    @Test
    fun times_Matrix3() {
        // 逆行列をかけて単位行列になることをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.random()
            if (mat.inv != null) {
                assertArrayEquals(Matrix3.identity.toArray(), (mat * mat.inv!!).toArray(), TOL)
                assertArrayEquals(Matrix3.identity.toArray(), (mat.inv!! * mat).toArray(), TOL)
            }
        }
        // 回転行列に回転行列をかけて正規直交行列になっていることをチェックする。
        for (count in 1..100) {
            val mat1 = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            val mat2 = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            if (mat1 != null && mat2 != null) assertTrue((mat1 * mat2).isOrthonormal)
        }
    }

    @Test
    fun createCsys() {
        // 各行が単位ベクトルで全て直交していることをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.createCsys(Vector3.random(), Vector3.random())
            assertTrue(mat.i.isUnit)
            assertTrue(mat.j.isUnit)
            assertTrue(mat.k.isUnit)
            assertEquals(0.0, mat.i dot mat.j, TOL)
            assertEquals(0.0, mat.j dot mat.k, TOL)
            assertEquals(0.0, mat.k dot mat.i, TOL)
        }
    }

    @Test
    fun createRotation() {
        // 各軸周り60度回転の回転行列が正しいことをチェックする。
        val rad = PI / 3
        val mat_x60deg = Matrix3.createRotation(Vector3.unitX, rad)!!
        assertArrayEquals(
            mat_x60deg.toArray(),
            Matrix3(Vector3.unitX, Vector3(0.0, cos(rad), sin(rad)), Vector3(0.0,-sin(rad), cos(rad))).toArray(),
            TOL
        )
        val mat_y60deg = Matrix3.createRotation(Vector3.unitY, rad)!!
        assertArrayEquals(
            mat_y60deg.toArray(),
            Matrix3(Vector3(cos(rad), 0.0, -sin(rad)), Vector3.unitY, Vector3(sin(rad),0.0, cos(rad))).toArray(),
            TOL
        )
        val mat_z60deg = Matrix3.createRotation(Vector3.unitZ, rad)!!
        assertArrayEquals(
            mat_z60deg.toArray(),
            Matrix3(Vector3(cos(rad), sin(rad), 0.0), Vector3(-sin(rad), cos(rad), 0.0), Vector3.unitZ).toArray(),
            TOL
        )
        // 各行が単位ベクトルで全て直交していることをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            if (mat != null) {
                assertTrue(mat.i.isUnit)
                assertTrue(mat.j.isUnit)
                assertTrue(mat.k.isUnit)
                assertEquals(0.0, mat.i dot mat.j, TOL)
                assertEquals(0.0, mat.j dot mat.k, TOL)
                assertEquals(0.0, mat.k dot mat.i, TOL)
            }
        }
    }
}