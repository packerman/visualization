package framework.math

import kotlin.math.sqrt

data class Vector3(internal val floats: FloatArray) {

    constructor() : this(FloatArray(3))

    constructor(x: Float, y: Float, z: Float) : this(floatArrayOf(x, y, z))

    operator fun get(i: Int): Float = floats[i]

    operator fun set(i: Int, value: Float) {
        floats[i] = value
    }

    var x: Float
        get() = this[0]
        set(value) {
            this[0] = value
        }

    var y: Float
        get() = this[1]
        set(value) {
            this[1] = value
        }

    var z: Float
        get() = this[2]
        set(value) {
            this[2] = value
        }

    var r: Float
        get() = this[0]
        set(value) {
            this[0] = value
        }

    var g: Float
        get() = this[1]
        set(value) {
            this[1] = value
        }

    var b: Float
        get() = this[2]
        set(value) {
            this[2] = value
        }

    fun copyTo(other: Vector3) {
        other.x = x
        other.y = y
        other.z = z
    }

    fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    operator fun plus(other: Vector3): Vector3 = Vector3(
        x + other.x,
        y + other.y,
        z + other.z
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

    fun asSequence(): Sequence<Float> = sequenceOf(x, y, z)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Vector3

        return floats.contentEquals(other.floats)
    }

    override fun hashCode(): Int {
        return floats.contentHashCode()
    }
}

