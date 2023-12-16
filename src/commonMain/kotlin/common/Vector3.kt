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

