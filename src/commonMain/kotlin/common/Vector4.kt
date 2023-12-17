package common

class Vector4(private var _x: Float,
              private var _y: Float,
              private var _z: Float,
              private var _w: Float) {

    val x: Float
        get() = _x

    val y: Float
        get() = _y

    val z: Float
        get() = _z

    val w: Float
        get() = _w

    fun copyTo(other: Vector4) {
        other._x = x
        other._y = y
        other._z = z
        other._w = w
    }

    fun set(x: Float, y: Float, z: Float, w: Float) {
        _x = x
        _y = y
        _z = z
        _w = w
    }
}