package framework.math.internal

import kotlin.math.*

private const val EPSILON = 0.000001f

internal fun identity(out: FloatArray): FloatArray {
    out[0] = 1f
    out[1] = 0f
    out[2] = 0f
    out[3] = 0f
    out[4] = 0f
    out[5] = 1f
    out[6] = 0f
    out[7] = 0f
    out[8] = 0f
    out[9] = 0f
    out[10] = 1f
    out[11] = 0f
    out[12] = 0f
    out[13] = 0f
    out[14] = 0f
    out[15] = 1f
    return out
}

internal fun fromTranslation(out: FloatArray, v: FloatArray): FloatArray {
    out[0] = 1f
    out[1] = 0f
    out[2] = 0f
    out[3] = 0f
    out[4] = 0f
    out[5] = 1f
    out[6] = 0f
    out[7] = 0f
    out[8] = 0f
    out[9] = 0f
    out[10] = 1f
    out[11] = 0f
    out[12] = v[0]
    out[13] = v[1]
    out[14] = v[2]
    out[15] = 1f
    return out
}

internal fun fromXRotation(out: FloatArray, rad: Float): FloatArray {
    val s = sin(rad)
    val c = cos(rad)

    out[0] = 1f
    out[1] = 0f
    out[2] = 0f
    out[3] = 0f
    out[4] = 0f
    out[5] = c
    out[6] = s
    out[7] = 0f
    out[8] = 0f
    out[9] = -s
    out[10] = c
    out[11] = 0f
    out[12] = 0f
    out[13] = 0f
    out[14] = 0f
    out[15] = 1f
    return out
}

internal fun fromYRotation(out: FloatArray, rad: Float): FloatArray {
    val s = sin(rad)
    val c = cos(rad)

    out[0] = c
    out[1] = 0f
    out[2] = -s
    out[3] = 0f
    out[4] = 0f
    out[5] = 1f
    out[6] = 0f
    out[7] = 0f
    out[8] = s
    out[9] = 0f
    out[10] = c
    out[11] = 0f
    out[12] = 0f
    out[13] = 0f
    out[14] = 0f
    out[15] = 1f
    return out
}

internal fun fromZRotation(out: FloatArray, rad: Float): FloatArray {
    val s = sin(rad)
    val c = cos(rad)

    out[0] = c
    out[1] = s
    out[2] = 0f
    out[3] = 0f
    out[4] = -s
    out[5] = c
    out[6] = 0f
    out[7] = 0f
    out[8] = 0f
    out[9] = 0f
    out[10] = 1f
    out[11] = 0f
    out[12] = 0f
    out[13] = 0f
    out[14] = 0f
    out[15] = 1f
    return out
}

internal fun fromScaling(out: FloatArray, v: FloatArray): FloatArray {
    out[0] = v[0]
    out[1] = 0f
    out[2] = 0f
    out[3] = 0f
    out[4] = 0f
    out[5] = v[1]
    out[6] = 0f
    out[7] = 0f
    out[8] = 0f
    out[9] = 0f
    out[10] = v[2]
    out[11] = 0f
    out[12] = 0f
    out[13] = 0f
    out[14] = 0f
    out[15] = 1f
    return out
}

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

    var b0 = b[0]
    var b1 = b[1]
    var b2 = b[2]
    var b3 = b[3]
    out[0] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
    out[1] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
    out[2] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
    out[3] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33

    b0 = b[4]
    b1 = b[5]
    b2 = b[6]
    b3 = b[7]
    out[4] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
    out[5] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
    out[6] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
    out[7] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33

    b0 = b[8]
    b1 = b[9]
    b2 = b[10]
    b3 = b[11]
    out[8] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
    out[9] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
    out[10] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
    out[11] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33

    b0 = b[12]
    b1 = b[13]
    b2 = b[14]
    b3 = b[15]
    out[12] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
    out[13] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
    out[14] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
    out[15] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33
    return out
}

internal fun invert(out: FloatArray, a: FloatArray): FloatArray? {
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

    val b00 = a00 * a11 - a01 * a10
    val b01 = a00 * a12 - a02 * a10
    val b02 = a00 * a13 - a03 * a10
    val b03 = a01 * a12 - a02 * a11
    val b04 = a01 * a13 - a03 * a11
    val b05 = a02 * a13 - a03 * a12
    val b06 = a20 * a31 - a21 * a30
    val b07 = a20 * a32 - a22 * a30
    val b08 = a20 * a33 - a23 * a30
    val b09 = a21 * a32 - a22 * a31
    val b10 = a21 * a33 - a23 * a31
    val b11 = a22 * a33 - a23 * a32

    var det =
        b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06

    if (det == 0f) {
        return null
    }
    det = 1f / det

    out[0] = (a11 * b11 - a12 * b10 + a13 * b09) * det
    out[1] = (a02 * b10 - a01 * b11 - a03 * b09) * det
    out[2] = (a31 * b05 - a32 * b04 + a33 * b03) * det
    out[3] = (a22 * b04 - a21 * b05 - a23 * b03) * det
    out[4] = (a12 * b08 - a10 * b11 - a13 * b07) * det
    out[5] = (a00 * b11 - a02 * b08 + a03 * b07) * det
    out[6] = (a32 * b02 - a30 * b05 - a33 * b01) * det
    out[7] = (a20 * b05 - a22 * b02 + a23 * b01) * det
    out[8] = (a10 * b10 - a11 * b08 + a13 * b06) * det
    out[9] = (a01 * b08 - a00 * b10 - a03 * b06) * det
    out[10] = (a30 * b04 - a31 * b02 + a33 * b00) * det
    out[11] = (a21 * b02 - a20 * b04 - a23 * b00) * det
    out[12] = (a11 * b07 - a10 * b09 - a12 * b06) * det
    out[13] = (a00 * b09 - a01 * b07 + a02 * b06) * det
    out[14] = (a31 * b01 - a30 * b03 - a32 * b00) * det
    out[15] = (a20 * b03 - a21 * b01 + a22 * b00) * det

    return out
}

internal fun transpose(out: FloatArray, a: FloatArray): FloatArray {
    if (out === a) {
        val a01 = a[1]
        val a02 = a[2]
        val a03 = a[3]
        val a12 = a[6]
        val a13 = a[7]
        val a23 = a[11]

        out[1] = a[4]
        out[2] = a[8]
        out[3] = a[12]
        out[4] = a01
        out[6] = a[9]
        out[7] = a[13]
        out[8] = a02
        out[9] = a12
        out[11] = a[14]
        out[12] = a03
        out[13] = a13
        out[14] = a23
    } else {
        out[0] = a[0]
        out[1] = a[4]
        out[2] = a[8]
        out[3] = a[12]
        out[4] = a[1]
        out[5] = a[5]
        out[6] = a[9]
        out[7] = a[13]
        out[8] = a[2]
        out[9] = a[6]
        out[10] = a[10]
        out[11] = a[14]
        out[12] = a[3]
        out[13] = a[7]
        out[14] = a[11]
        out[15] = a[15]
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

internal fun getTranslation(out: FloatArray, mat: FloatArray): FloatArray {
    out[0] = mat[12]
    out[1] = mat[13]
    out[2] = mat[14]

    return out
}

internal fun setTranslation(out: FloatArray, v: FloatArray): FloatArray {
    out[12] = v[0]
    out[13] = v[1]
    out[14] = v[2]

    return out
}

internal fun lookAt(out: FloatArray, eye: FloatArray, center: FloatArray, up: FloatArray): FloatArray {
    val eyeX = eye[0]
    val eyeY = eye[1]
    val eyeZ = eye[2]
    val upx = up[0]
    val upy = up[1]
    val upz = up[2]
    val centerX = center[0]
    val centerY = center[1]
    val centerZ = center[2]

    if (abs((eyeX - centerX).toDouble()) < EPSILON && abs((eyeY - centerY).toDouble()) < EPSILON && abs(
            (eyeZ - centerZ).toDouble()
        ) < EPSILON
    ) {
        return identity(out)
    }

    var z0 = eyeX - centerX
    var z1 = eyeY - centerY
    var z2 = eyeZ - centerZ

    var len = 1 / sqrt(z0 * z0 + z1 * z1 + z2 * z2)
    z0 *= len
    z1 *= len
    z2 *= len

    var x0 = upy * z2 - upz * z1
    var x1 = upz * z0 - upx * z2
    var x2 = upx * z1 - upy * z0
    len = sqrt(x0 * x0 + x1 * x1 + x2 * x2)
    if (len == 0f) {
        x0 = 0f
        x1 = 0f
        x2 = 0f
    } else {
        len = 1 / len
        x0 *= len
        x1 *= len
        x2 *= len
    }

    var y0 = z1 * x2 - z2 * x1
    var y1 = z2 * x0 - z0 * x2
    var y2 = z0 * x1 - z1 * x0

    len = sqrt(y0 * y0 + y1 * y1 + y2 * y2)
    if (len == 0f) {
        y0 = 0f
        y1 = 0f
        y2 = 0f
    } else {
        len = 1 / len
        y0 *= len
        y1 *= len
        y2 *= len
    }

    out[0] = x0
    out[1] = y0
    out[2] = z0
    out[3] = 0f
    out[4] = x1
    out[5] = y1
    out[6] = z1
    out[7] = 0f
    out[8] = x2
    out[9] = y2
    out[10] = z2
    out[11] = 0f
    out[12] = -(x0 * eyeX + x1 * eyeY + x2 * eyeZ)
    out[13] = -(y0 * eyeX + y1 * eyeY + y2 * eyeZ)
    out[14] = -(z0 * eyeX + z1 * eyeY + z2 * eyeZ)
    out[15] = 1f

    return out
}
