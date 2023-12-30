package framework.math

import framework.math.internal.multiply
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data class Matrix4 internal constructor(val floats: FloatArray) {

    init {
        require(floats.size == 16)
    }

    constructor() : this(FloatArray(16))

    operator fun get(i: Int) = floats[i]

    private operator fun set(i: Int, value: Float) {
        floats[i] = value
    }

    operator fun times(other: Matrix4): Matrix4 = times(other, Matrix4())

    fun times(other: Matrix4, result: Matrix4): Matrix4 {
        multiply(result.floats, this.floats, other.floats)
        return result
    }

    inline fun forEachIndexed(action: (Int, Float) -> Unit) {
        floats.forEachIndexed(action)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Matrix4

        return floats.contentEquals(other.floats)
    }

    override fun hashCode(): Int {
        return floats.contentHashCode()
    }

    companion object {

        fun identity() = Matrix4(
            floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
        )

        fun translation(x: Float, y: Float, z: Float) = Matrix4(
            floatArrayOf(
                1f, 0f, 0f, x,
                0f, 1f, 0f, y,
                0f, 0f, 1f, z,
                0f, 0f, 0f, 1f
            )
        )

        fun rotationX(angle: Float): Matrix4 {
            val c = cos(angle)
            val s = sin(angle)
            return Matrix4(
                floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, c, -s, 0f,
                    0f, s, c, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun rotationY(angle: Float): Matrix4 {
            val c = cos(angle)
            val s = sin(angle)
            return Matrix4(
                floatArrayOf(
                    c, 0f, s, 0f,
                    0f, 1f, 0f, 0f,
                    -s, 0f, c, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun rotationZ(angle: Float): Matrix4 {
            val c = cos(angle)
            val s = sin(angle)
            return Matrix4(
                floatArrayOf(
                    c, -s, 0f, 0f,
                    s, c, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun scale(s: Float) = Matrix4(
            floatArrayOf(
                s, 0f, 0f, 0f,
                0f, s, 0f, 0f,
                0f, 0f, s, 0f,
                0f, 0f, 0f, 1f
            )
        )

        fun perspective(
            angleOfView: Float = toRadians(60f),
            aspectRatio: Float = 1f,
            near: Float = 0.1f,
            far: Float = 1000f
        ): Matrix4 {
            val a = angleOfView
            val d = 1f / tan(a / 2f)
            val r = aspectRatio
            val b = (far + near) / (near - far)
            val c = 2f * far * near / (near - far)
            return Matrix4(
                floatArrayOf(
                    d / r, 0f, 0f, 0f,
                    0f, d, 0f, 0f,
                    0f, 0f, b, c,
                    0f, 0f, -1f, 0f
                )
            )
        }
    }
}
