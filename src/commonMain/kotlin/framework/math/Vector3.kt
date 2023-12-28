package framework.math

import kotlin.math.sqrt

data class Vector3(private var _x: Float, private var _y: Float, private var _z: Float) {

    val x: Float
        get() = _x

    val y: Float
        get() = _y

    val z: Float
        get() = _z

    fun copyTo(other: Vector3) {
        other._x = x
        other._y = y
        other._z = z
    }

    fun set(x: Float, y: Float, z: Float) {
        _x = x
        _y = y
        _z = z
    }

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

    operator fun div(factor: Float): Vector3 = Vector3(
        x / factor,
        y / factor,
        z / factor
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

    fun asSequence(): Sequence<Float> = sequenceOf(_x, _y, _z)
}

