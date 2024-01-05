package framework.math

class Vector2(internal val floats: FloatArray) {

    constructor() : this(FloatArray(2))

    constructor(x: Float, y: Float) : this(floatArrayOf(x, y))

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
}
