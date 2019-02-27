import kotlin.math.*
import kotlin.random.Random
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class Matrix3Test {

    /** 許容値 */
    private val TOL = 1e-8

    /** Vector3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Vector3.toArray() = doubleArrayOf(x, y, z)

    /** Matrix3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Matrix3.toArray() = doubleArrayOf(m11, m12, m13, m21, m22, m23, m31, m32, m33)

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
            val mat = Matrix3.random()
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
            if (mat.inv != null) {
                assertArrayEquals(vec.toArray(), (mat.inv!! * (mat * vec)).toArray(), TOL)
            }
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
            if (mat1 != null && mat2 != null) assertTrue((mat1 * mat2).isOrthogonal!!)
        }
    }

    @Test
    fun createScale() {
        // XYZ軸方向のスケーリング行列が正しいことをチェックする。
        for (count in 1..100) {
            val scale = Random.nextDouble()
            val matX = Matrix3.createScale(Vector3.unitX, scale)
            val matY = Matrix3.createScale(Vector3.unitY, scale)
            val matZ = Matrix3.createScale(Vector3.unitZ, scale)
            assertArrayEquals(matX.toArray(), Matrix3.createDiag(scale, 1.0, 1.0).toArray(), TOL)
            assertArrayEquals(matY.toArray(), Matrix3.createDiag(1.0, scale, 1.0).toArray(), TOL)
            assertArrayEquals(matZ.toArray(), Matrix3.createDiag(1.0, 1.0, scale).toArray(), TOL)
        }
        // スケーリング方向に平行なベクトルが定数倍になることをチェックする。
        for (count in 1..100) {
            val scale = Random.nextDouble()
            val axis = Vector3.random()
            val mat = Matrix3.createScale(axis, scale)
            val vec = axis * 2.0
            assertArrayEquals((mat * vec).toArray(), (vec * scale).toArray(), TOL)
        }
    }

    @Test
    fun createCsys() {
        // 各行が単位ベクトルで全て直交していることをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.createCsys(Vector3.random(), Vector3.random())
            val (i, j, k) = mat.cols
            assertTrue(i.isUnit)
            assertTrue(j.isUnit)
            assertTrue(k.isUnit)
            assertEquals(0.0, i dot j, TOL)
            assertEquals(0.0, j dot k, TOL)
            assertEquals(0.0, k dot i, TOL)
        }
    }

    @Test
    fun createRotation() {
        // 各軸周り60度回転の回転行列が正しいことをチェックする。
        val rad = PI / 3
        val mat_x60deg = Matrix3.createRotation(Vector3.unitX, rad)!!
        assertArrayEquals(
            mat_x60deg.toArray(),
            Matrix3.ofCols(Vector3.unitX, Vector3(0.0, cos(rad), sin(rad)), Vector3(0.0,-sin(rad), cos(rad))).toArray(),
            TOL
        )
        val mat_y60deg = Matrix3.createRotation(Vector3.unitY, rad)!!
        assertArrayEquals(
            mat_y60deg.toArray(),
            Matrix3.ofCols(Vector3(cos(rad), 0.0, -sin(rad)), Vector3.unitY, Vector3(sin(rad),0.0, cos(rad))).toArray(),
            TOL
        )
        val mat_z60deg = Matrix3.createRotation(Vector3.unitZ, rad)!!
        assertArrayEquals(
            mat_z60deg.toArray(),
            Matrix3.ofCols(Vector3(cos(rad), sin(rad), 0.0), Vector3(-sin(rad), cos(rad), 0.0), Vector3.unitZ).toArray(),
            TOL
        )
        // 各行が単位ベクトルで全て直交していることをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            if (mat != null) {
                val (i, j, k) = mat.cols
                assertTrue(i.isUnit)
                assertTrue(j.isUnit)
                assertTrue(k.isUnit)
                assertEquals(0.0, i dot j, TOL)
                assertEquals(0.0, j dot k, TOL)
                assertEquals(0.0, k dot i, TOL)
            }
        }
    }
}