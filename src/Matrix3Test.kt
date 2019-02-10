import kotlin.math.*
import kotlin.random.Random
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class Matrix3Test {

    @Test
    fun getDet() {
        // 単位行列の定数倍の行列式
        for (count in 1..100) {
            val k = Random.nextDouble()
            assertTrue((Matrix3.identity * k).det.isClose(k.pow(3)))
        }
        // 対角行列の行列式
        for (count in 1..100) {
            val i = Random.nextDouble()
            val j = Random.nextDouble()
            val k = Random.nextDouble()
            assertTrue(Matrix3.createDiag(i, j, k).det.isClose((i * j * k)))
        }
        // 転置・逆行列の行列式
        for (count in 1..100) {
            val mat = Matrix3.random()
            assertTrue(mat.T.det.isClose(mat.det))
        }
        // 積の行列式
        for (count in 1..100) {
            val mat1 = Matrix3.random()
            val mat2 = Matrix3.random()
            assertTrue((mat1 * mat2).det.isClose(mat1.det * mat2.det))
        }
    }

    fun getT() {
        for (count in 1..100) {
            val mat = Matrix3.random()
            assertEquals(mat, mat.T.T)
        }
    }

    @Test
    fun times_Vector3() {
        // 単位行列の定数倍をかける
        for (count in 1..100) {
            val vec = Vector3.random()
            val k = Random.nextDouble()
            assertEquals(vec , Matrix3.identity * vec)
            assertEquals(vec * k, (Matrix3.identity * k) * vec)
        }
        // 行列をかけてさらに逆行列をかけた時に元のベクトルに戻ることをチェックする
        for (count in 1..100) {
            val vec = Vector3.random()
            val mat = Matrix3.random()
            assertTrue((mat.inv!! * (mat * vec)).isClose(vec))
        }
        // 回転行列をかけて長さが変わらないことをチェックする
        for (count in 1..100) {
            val vec = Vector3.random()
            val mat = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            assertTrue((mat * vec).length.isClose(vec.length))
        }
    }

    @Test
    fun times_Matrix3() {
        // 逆行列をかけて単位行列になることをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.random()
            if (mat.inv != null) {
                assertTrue((mat * mat.inv!!).isClose(Matrix3.identity))
                assertTrue((mat.inv!! * mat).isClose(Matrix3.identity))
            }
        }
        // 回転行列に回転行列をかけて正規直交行列になっていることをチェックする。
        for (count in 1..100) {
            val mat1 = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            val mat2 = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            assertTrue((mat1 * mat2).isOrthogonal)
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
            assertTrue((mat.i dot mat.j).isClose(0.0))
            assertTrue((mat.j dot mat.k).isClose(0.0))
            assertTrue((mat.k dot mat.i).isClose(0.0))
        }
    }

    @Test
    fun createRotation() {
        // 各行が単位ベクトルで全て直交していることをチェックする。
        for (count in 1..100) {
            val mat = Matrix3.createCsys(Vector3.random(), Vector3.random())
            assertTrue(mat.i.isUnit)
            assertTrue(mat.j.isUnit)
            assertTrue(mat.k.isUnit)
            assertTrue((mat.i dot mat.j).isClose(0.0))
            assertTrue((mat.j dot mat.k).isClose(0.0))
            assertTrue((mat.k dot mat.i).isClose(0.0))
        }
    }
}