import kotlin.math.*
import kotlin.random.Random
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class Matrix4Test {

    /** 許容値 */
    private val TOL = 1e-9

    /** Vector3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Vector3.toArray() = doubleArrayOf(x, y, z)

    /** Matrix3をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Matrix3.toArray() = doubleArrayOf(m11, m12, m13, m21, m22, m23, m31, m32, m33)

    /** Matrix4をJava配列に変換する（assertArrayEqualsに渡すため） */
    private fun Matrix4.toArray() = this.rows.flatten().toDoubleArray()

    @Test
    fun getCols() {
        // 各列の値をチェック
        for (count in 1..100) {
            val rot = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            val trans = Vector3.random()
            if (rot != null) {
                val mat = Matrix4(rot, trans)
                assertArrayEquals(mat.cols[0].toDoubleArray(), (rot.cols[0].toArray() + 0.0), TOL)
                assertArrayEquals(mat.cols[1].toDoubleArray(), (rot.cols[1].toArray() + 0.0), TOL)
                assertArrayEquals(mat.cols[2].toDoubleArray(), (rot.cols[2].toArray() + 0.0), TOL)
                assertArrayEquals(mat.cols[3].toDoubleArray(), (trans.toArray() + 1.0), TOL)
            }
        }
    }

    @Test
    fun getRows() {
        for (count in 1..100) {
            val rot = Matrix3.createRotation(Vector3.random(), Random.nextDouble())
            val trans = Vector3.random()
            if (rot != null) {
                val mat = Matrix4(rot, trans)
                assertArrayEquals(mat.rows[0].toDoubleArray(), (rot.rows[0].toArray() + trans.x), TOL)
                assertArrayEquals(mat.rows[1].toDoubleArray(), (rot.rows[1].toArray() + trans.y), TOL)
                assertArrayEquals(mat.rows[2].toDoubleArray(), (rot.rows[2].toArray() + trans.z), TOL)
                assertArrayEquals(mat.rows[3].toDoubleArray(), doubleArrayOf(0.0, 0.0, 0.0, 1.0), TOL)
            }
        }
    }

    @Test
    fun getInv() {
        // 行列に逆行列をかけると単位行列になることをチェックする
        for (count in 1..100) {
            val mat = Matrix4.random()
            if (mat.inv != null) {
                assertArrayEquals(Matrix4.identity.toArray(), (mat * mat.inv!!).toArray(), TOL)
            }
        }
    }

    @Test
    fun times_Matrix4() {
        // 逆行列をかけて単位行列になることをチェックする。
        for (count in 1..100) {
            val mat = Matrix4.random()
            if (mat.inv != null) {
                assertArrayEquals(Matrix4.identity.toArray(), (mat * mat.inv!!).toArray(), TOL)
                assertArrayEquals(Matrix4.identity.toArray(), (mat.inv!! * mat).toArray(), TOL)
            }
        }
        // 回転の後に並進をかけて正しい結果になっていることチェックする。
        for (count in 1..100) {
            val matR = Matrix4.createRotation(Vector3.random(), Random.nextDouble())
            val matT = Matrix4.createTranslation(Vector3.random())
            if (matR != null) {
                assertArrayEquals(Matrix4(matR.rot, matT.trans).toArray(), (matT * matR).toArray(), TOL)
            }
        }
        // 並進の後に回転をかけて正しい結果になっていることをチェッkする。
        for (count in 1..100) {
            val matR = Matrix4.createRotation(Vector3.random(), Random.nextDouble())
            val matT = Matrix4.createTranslation(Vector3.random())
            if (matR != null) {
                assertArrayEquals(
                    Matrix4(matR.rot, matR.rot * matT.trans).toArray(), (matR * matT).toArray(), TOL
                )
            }
        }
    }

    @Test
    fun transform() {
        // 単位行列をかける
        for (count in 1..100) {
            val vec = Vector3.random()
            val k = Random.nextDouble()
            assertArrayEquals(vec.toArray(), (Matrix4.identity.transform(vec)).toArray(), TOL)
        }
        // 行列をかけてさらに逆行列をかけた時に元のベクトルに戻ることをチェックする
        for (count in 1..100) {
            val vec = Vector3.random()
            val mat = Matrix4.random()
            if (mat.inv != null) {
                assertArrayEquals(vec.toArray(), mat.inv!!.transform(mat.transform(vec)).toArray(), TOL)
            }
        }
        // 回転行列をかけて長さが変わらないことをチェックする
        for (count in 1..100) {
            val vec = Vector3.random()
            val mat = Matrix4.createRotation(Vector3.random(), Random.nextDouble())
            if (mat != null) {
                assertEquals(vec.length, mat.transform(vec).length, TOL)
            }
        }
    }

    @Test
    fun createTranslation() {
        // 移動後の点が移動前の点＋移動量になっていることをチェックする。
        for (count in 1..100) {
            val trans = Vector3.random()
            val mat = Matrix4.createTranslation(trans)
            val vec = Vector3.random()
            assertArrayEquals((vec + trans).toArray(), mat.transform(vec).toArray(), TOL)
        }
    }

    @Test
    fun createScale() {
        // XYZ軸方向のスケーリング行列が正しいことをチェックする。
        for (count in 1..100) {
            val scale = Random.nextDouble()
            val matX = Matrix4.createScale(Vector3.unitX, scale)
            val matY = Matrix4.createScale(Vector3.unitY, scale)
            val matZ = Matrix4.createScale(Vector3.unitZ, scale)
            assertArrayEquals(matX.rot.toArray(), Matrix3.createDiag(scale, 1.0, 1.0).toArray(), TOL)
            assertArrayEquals(matY.rot.toArray(), Matrix3.createDiag(1.0, scale, 1.0).toArray(), TOL)
            assertArrayEquals(matZ.rot.toArray(), Matrix3.createDiag(1.0, 1.0, scale).toArray(), TOL)
        }
        // スケーリング方向に平行なベクトルが定数倍になることをチェックする。
        for (count in 1..100) {
            val scale = Random.nextDouble()
            val axis = Vector3.random()
            val mat = Matrix4.createScale(axis, scale)
            val vec = axis * 2.0
            assertArrayEquals((mat.transform(vec)).toArray(), (vec * scale).toArray(), TOL)
        }
    }

    @Test
    fun createProjection() {
        // 投影面の中心から投影点に向かうベクトルが投影面の法線ベクトルに垂直であることをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val center = Vector3.random()
            val mat = Matrix4.createProjection(axis, center)
            val point = Vector3.random()
            assertEquals(0.0, (mat.transform(point) - center) dot axis, TOL)
        }
    }

    @Test
    fun createMirror() {
        // ミラーリング前後の点を結ぶベクトルが対称面の法線ベクトルに平行であることをチェックする。
        // ミラーリング前後で対称面の中心点からの距離が変化しないことをチェックする。
        for (count in 1..100) {
            val axis = Vector3.random()
            val center = Vector3.random()
            val mat = Matrix4.createMirror(axis, center)
            val point = Vector3.random()
            assertEquals(0.0, ((mat.transform(point) - point) cross axis).length, TOL)
            assertEquals((point - center).length, (mat.transform(point) - center).length, TOL)
        }
    }

    @Test
    fun createCsys() {
        // 回転行列部分の各行が単位ベクトルで全て直交していることをチェックする。
        for (count in 1..100) {
            val mat = Matrix4.createCsys(Vector3.random(), Vector3.random())
            val (i, j, k) = mat.rot.cols
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
        // 回転行列部分の各行が単位ベクトルで全て直交していることをチェックする。
        for (count in 1..100) {
            val mat = Matrix4.createCsys(Vector3.random(), Vector3.random())
            val (i, j, k) = mat.rot.cols
            assertTrue(i.isUnit)
            assertTrue(j.isUnit)
            assertTrue(k.isUnit)
            assertEquals(0.0, i dot j, TOL)
            assertEquals(0.0, j dot k, TOL)
            assertEquals(0.0, k dot i, TOL)
        }
    }

    @Test
    fun createMove() {
        // 移動元の座標系の原点が、移動先の座標系の原点に移動することをチェックする。
        // 移動元の座標系のXYZ軸が、移動先の座標系のXYZ軸に移動することをチェックする。
        for (count in 1..100) {
            val start = Matrix4.createCsys(Vector3.random(), Vector3.random(), Vector3.random())
            val end = Matrix4.createCsys(Vector3.random(), Vector3.random(), Vector3.random())
            val mat = Matrix4.createMove(start, end)
            if (mat != null) {
                assertArrayEquals(end.trans.toArray(), mat.transform(start.trans).toArray(), TOL)
                assertArrayEquals(
                    (end.rot.cols[0] + end.trans).toArray(),
                    mat.transform(start.rot.cols[0] + start.trans).toArray(),
                    TOL
                )
                assertArrayEquals(
                    (end.rot.cols[1] + end.trans).toArray(),
                    mat.transform(start.rot.cols[1] + start.trans).toArray(),
                    TOL
                )
                assertArrayEquals(
                    (end.rot.cols[2] + end.trans).toArray(),
                    mat.transform(start.rot.cols[2] + start.trans).toArray(),
                    TOL
                )
            }
        }
    }
}