package common

class Vector1(private var _x: Float) {
    val x: Float
        get() = _x

    fun set(x: Float) {
        _x = x
    }
}