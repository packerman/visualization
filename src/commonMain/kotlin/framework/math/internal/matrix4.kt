package framework.math.internal

import kotlin.math.tan

internal fun copy(out: FloatArray, a: FloatArray): FloatArray {
    out[0] = a[0]
    out[1] = a[1]
    out[2] = a[2]
    out[3] = a[3]
    out[4] = a[4]
    out[5] = a[5]
    out[6] = a[6]
    out[7] = a[7]
    out[8] = a[8]
    out[9] = a[9]
    out[10] = a[10]
    out[11] = a[11]
    out[12] = a[12]
    out[13] = a[13]
    out[14] = a[14]
    out[15] = a[15]
    return out
}

internal fun multiply(out: FloatArray, a: FloatArray, b: FloatArray): FloatArray {
    val a00 = a[0]
    val a01 = a[1]
    val a02 = a[2]
    val a03 = a[3]
    val a10 = a[4]
    val a11 = a[5]
    val a12 = a[6]
    val a13 = a[7]
    val a20 = a[8]
    val a21 = a[9]
    val a22 = a[10]
    val a23 = a[11]
    val a30 = a[12]
    val a31 = a[13]
    val a32 = a[14]
    val a33 = a[15]

    run {
        val b0 = b[0]
        val b1 = b[1]
        val b2 = b[2]
        val b3 = b[3]
        out[0] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
        out[1] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
        out[2] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
        out[3] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33
    }
    run {
        val b0 = b[4]
        val b1 = b[5]
        val b2 = b[6]
        val b3 = b[7]
        out[4] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
        out[5] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
        out[6] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
        out[7] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33
    }
    run {
        val b0 = b[8]
        val b1 = b[9]
        val b2 = b[10]
        val b3 = b[11]
        out[8] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
        out[9] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
        out[10] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
        out[11] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33
    }
    run {
        val b0 = b[12]
        val b1 = b[13]
        val b2 = b[14]
        val b3 = b[15]
        out[12] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
        out[13] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
        out[14] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
        out[15] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33
    }
    return out
}

internal fun perspectiveNO(out: FloatArray, fovy: Float, aspect: Float, near: Float, far: Float?): FloatArray {
    val f = 1f / tan(fovy / 2)
    out[0] = f / aspect
    out[1] = 0f
    out[2] = 0f
    out[3] = 0f
    out[4] = 0f
    out[5] = f
    out[6] = 0f
    out[7] = 0f
    out[8] = 0f
    out[9] = 0f
    out[11] = -1f
    out[12] = 0f
    out[13] = 0f
    out[15] = 0f
    if (far != null && far != Float.POSITIVE_INFINITY) {
        val nf = 1f / (near - far)
        out[10] = (far + near) * nf
        out[14] = 2 * far * near * nf
    } else {
        out[10] = -1f
        out[14] = -2f * near
    }
    return out
}
