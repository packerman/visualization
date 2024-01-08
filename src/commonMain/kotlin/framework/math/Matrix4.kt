package framework.math

import framework.math.internal.*

data class Matrix4 internal constructor(val floats: FloatArray) {

    init {
        require(floats.size == 16)
    }

    constructor() : this(FloatArray(16))

    operator fun get(i: Int) = floats[i]

    private operator fun set(i: Int, value: Float) {
        floats[i] = value
    }

    operator fun times(other: Matrix4): Matrix4 = Matrix4(multiply(FloatArray(16), floats, other.floats))

    operator fun timesAssign(other: Matrix4) {
        multiply(floats, floats, other.floats)
    }

    fun preMultiply(other: Matrix4) {
        multiply(floats, other.floats, floats)
    }

    fun invert(result: Matrix4): Matrix4 {
        invert(result.floats, floats)
        return result
    }

    fun transpose(result: Matrix4): Matrix4 {
        transpose(result.floats, floats)
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

        fun identity() = Matrix4(identity(FloatArray(16)))

        fun translation(x: Float, y: Float, z: Float) = Matrix4(fromTranslation(FloatArray(16), floatArrayOf(x, y, z)))

        fun rotationX(angle: Float) = Matrix4(fromXRotation(FloatArray(16), angle))

        fun rotationY(angle: Float) = Matrix4(fromYRotation(FloatArray(16), angle))

        fun rotationZ(angle: Float) = Matrix4(fromZRotation(FloatArray(16), angle))

        fun scale(s: Float) = Matrix4(fromScaling(FloatArray(16), floatArrayOf(s, s, s)))

        fun perspective(
            angleOfView: Float = toRadians(60f),
            aspectRatio: Float = 1f,
            near: Float = 0.1f,
            far: Float = 1000f
        ) = perspective(Matrix4(), angleOfView, aspectRatio, near, far)

        fun perspective(
            result: Matrix4,
            angleOfView: Float = toRadians(60f),
            aspectRatio: Float = 1f,
            near: Float = 0.1f,
            far: Float = 1000f
        ): Matrix4 {
            perspectiveNO(result.floats, angleOfView, aspectRatio, near, far)
            return result
        }

        fun lookAt(result: Matrix4, eye: Vector3, center: Vector3, up: Vector3): Matrix4 {
            lookAt(result.floats, eye.floats, center.floats, up.floats)
            return result
        }
    }
}
