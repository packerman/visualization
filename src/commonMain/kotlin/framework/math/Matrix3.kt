package framework.math

import framework.math.internal.fromMat4
import framework.math.internal.transformMat3

class Matrix3 internal constructor(val floats: FloatArray) {

    init {
        require(floats.size == 9)
    }

    constructor() : this(FloatArray(9))

    operator fun get(i: Int) = floats[i]

    private operator fun set(i: Int, value: Float) {
        floats[i] = value
    }

    operator fun times(a: Vector3): Vector3 =
        Vector3(transformMat3(FloatArray(3), a.floats, floats))

    companion object {
        fun fromMatrix4(a: Matrix4): Matrix3 = Matrix3(fromMat4(FloatArray(9), a.floats))
    }
}
