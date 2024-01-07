package framework.math.internal

internal fun transformMat3(out: FloatArray, a: FloatArray, m: FloatArray): FloatArray {
    val x = a[0]
    val y = a[1]
    val z = a[2]
    out[0] = x * m[0] + y * m[3] + z * m[6]
    out[1] = x * m[1] + y * m[4] + z * m[7]
    out[2] = x * m[2] + y * m[5] + z * m[8]
    return out
}
