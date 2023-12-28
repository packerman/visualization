package framework.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data class Matrix4 internal constructor(val a: FloatArray) {

    init {
        require(a.size == 16)
    }

    constructor() : this(FloatArray(16)) {
        this[0] = 1f
        this[5] = 1f
        this[10] = 1f
        this[15] = 1f
    }

    private operator fun get(i: Int) = a[i]

    private operator fun set(i: Int, value: Float) {
        a[i] = value
    }

    inline fun forEachIndexed(action: (Int, Float) -> Unit) = a.forEachIndexed(action)

    fun copyTo(destination: Matrix4): Matrix4 {
        for (i in 0..15) {
            destination[i] = this[i]
        }
        return destination
    }

    fun identity() = identity(this)

    fun perspective(fovy: Float, aspect: Float, near: Float, far: Float) =
        perspective(this, fovy, aspect, near, far)

    fun translate(out: Matrix4, v: Vector3) = translate(out, this, v)

    fun translate(v: Vector3) = translate(this, v)

    fun rotate(out: Matrix4, rad: Float, axis: Vector3) = rotate(out, this, rad, axis)

    fun rotate(rad: Float, axis: Vector3) = rotate(this, rad, axis)

    fun invert(out: Matrix4) = invert(out, this)

    fun invert() = invert(this)

    fun transpose(out: Matrix4): Matrix4 = transpose(out, this)

    fun transpose(): Matrix4 = transpose(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Matrix4

        return a.contentEquals(other.a)
    }

    override fun hashCode(): Int {
        return a.contentHashCode()
    }

    companion object {
        fun identity() = identity(Matrix4())

        fun identity(result: Matrix4): Matrix4 {
            for (i in 0..15) {
                result[i] = if (i % 5 == 0) 1f else 0f
            }
            return result
        }

        private fun zero(m: Matrix4): Matrix4 {
            for (i in 0..15) m[i] = 0f
            return m
        }

        fun translate(out: Matrix4, a: Matrix4, v: Vector3): Matrix4 {
            val x = v.x
            val y = v.y
            val z = v.z

            if (a === out) {
                out[12] = a[0] * x + a[4] * y + a[8] * z + a[12]
                out[13] = a[1] * x + a[5] * y + a[9] * z + a[13]
                out[14] = a[2] * x + a[6] * y + a[10] * z + a[14]
                out[15] = a[3] * x + a[7] * y + a[11] * z + a[15]
            } else {
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

                out[0] = a00
                out[1] = a01
                out[2] = a02
                out[3] = a03
                out[4] = a10
                out[5] = a11
                out[6] = a12
                out[7] = a13
                out[8] = a20
                out[9] = a21
                out[10] = a22
                out[11] = a23

                out[12] = a00 * x + a10 * y + a20 * z + a[12]
                out[13] = a01 * x + a11 * y + a21 * z + a[13]
                out[14] = a02 * x + a12 * y + a22 * z + a[14]
                out[15] = a03 * x + a13 * y + a23 * z + a[15]
            }

            return out
        }

        fun perspective(m: Matrix4, fovy: Float, aspect: Float, near: Float, far: Float): Matrix4 {
            zero(m)
            val f = 1f / tan(fovy / 2f)
            m[0] = f / aspect
            m[5] = f
            m[11] = -1f
            val nf = 1f / (near - far)
            m[10] = (far + near) * nf
            m[14] = 2f * far * near * nf
            return m
        }

        fun invert(out: Matrix4, a: Matrix4): Matrix4? {
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

            val det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06

            if (det == 0f) {
                return null
            }
            val inv = 1f / det

            out[0] = (a11 * b11 - a12 * b10 + a13 * b09) * inv
            out[1] = (a02 * b10 - a01 * b11 - a03 * b09) * inv
            out[2] = (a31 * b05 - a32 * b04 + a33 * b03) * inv
            out[3] = (a22 * b04 - a21 * b05 - a23 * b03) * inv
            out[4] = (a12 * b08 - a10 * b11 - a13 * b07) * inv
            out[5] = (a00 * b11 - a02 * b08 + a03 * b07) * inv
            out[6] = (a32 * b02 - a30 * b05 - a33 * b01) * inv
            out[7] = (a20 * b05 - a22 * b02 + a23 * b01) * inv
            out[8] = (a10 * b10 - a11 * b08 + a13 * b06) * inv
            out[9] = (a01 * b08 - a00 * b10 - a03 * b06) * inv
            out[10] = (a30 * b04 - a31 * b02 + a33 * b00) * inv
            out[11] = (a21 * b02 - a20 * b04 - a23 * b00) * inv
            out[12] = (a11 * b07 - a10 * b09 - a12 * b06) * inv
            out[13] = (a00 * b09 - a01 * b07 + a02 * b06) * inv
            out[14] = (a31 * b01 - a30 * b03 - a32 * b00) * inv
            out[15] = (a20 * b03 - a21 * b01 + a22 * b00) * inv
            return out
        }

        fun transpose(out: Matrix4, a: Matrix4): Matrix4 {
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

        fun rotate(out: Matrix4, a: Matrix4, rad: Float, axis: Vector3): Matrix4 {
            val len = axis.length
            val x = axis.x / len
            val y = axis.y / len
            val z = axis.z / len
            val s = sin(rad)
            val c = cos(rad)
            val t = 1 - c

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

            val b00 = x * x * t + c
            val b01 = y * x * t + z * s
            val b02 = z * x * t - y * s
            val b10 = x * y * t - z * s
            val b11 = y * y * t + c
            val b12 = z * y * t + x * s
            val b20 = x * z * t + y * s
            val b21 = y * z * t - x * s
            val b22 = z * z * t + c

            out[0] = a00 * b00 + a10 * b01 + a20 * b02
            out[1] = a01 * b00 + a11 * b01 + a21 * b02
            out[2] = a02 * b00 + a12 * b01 + a22 * b02
            out[3] = a03 * b00 + a13 * b01 + a23 * b02
            out[4] = a00 * b10 + a10 * b11 + a20 * b12
            out[5] = a01 * b10 + a11 * b11 + a21 * b12
            out[6] = a02 * b10 + a12 * b11 + a22 * b12
            out[7] = a03 * b10 + a13 * b11 + a23 * b12
            out[8] = a00 * b20 + a10 * b21 + a20 * b22
            out[9] = a01 * b20 + a11 * b21 + a21 * b22
            out[10] = a02 * b20 + a12 * b21 + a22 * b22
            out[11] = a03 * b20 + a13 * b21 + a23 * b22

            if (a !== out) {
                out[12] = a[12]
                out[13] = a[13]
                out[14] = a[14]
                out[15] = a[15]
            }
            return out
        }
    }
}
