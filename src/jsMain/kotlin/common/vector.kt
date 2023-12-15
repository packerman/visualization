package common

import kotlin.math.sqrt

data class Vector3(val x: Float, val y: Float, val z: Float) {

    operator fun plus(other: Vector3): Vector3 = Vector3(
        x + other.x,
        y + other.y,
        z + other.z
    )

    operator fun minus(other: Vector3): Vector3 = Vector3(
        x - other.x,
        y - other.y,
        z - other.z
    )

    operator fun times(factor: Float): Vector3 = Vector3(
        factor * x,
        factor * y,
        factor * z
    )

    val length: Float
        get() = sqrt(x * x + y * y + z * z)

    fun normalize(): Vector3 {
        val d = length
        return Vector3(x / d, y / d, z / d)
    }

    infix fun cross(other: Vector3): Vector3 = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
}

data class Matrix4 internal constructor(private val m: FloatArray) {

    init {
        require(m.size == 16)
    }

    constructor(vararg floats: Float) : this(floats)

    fun forEachIndexed(action: (Int, Float) -> Unit) = m.forEachIndexed(action)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Matrix4

        return m.contentEquals(other.m)
    }

    override fun hashCode(): Int {
        return m.contentHashCode()
    }

    companion object {
        fun identity() = Matrix4(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }
}
