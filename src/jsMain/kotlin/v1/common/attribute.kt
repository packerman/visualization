package v1.common

import web.gl.WebGLBuffer

@Deprecated(message = "")
class Attribute(private val arrayBuffer: WebGLBuffer, private val length: Int) {

    fun getCount(size: Int): Int {
        TODO()
    }

    companion object {
        fun attribute(floatArray: Array<Float>): Supplier<Attribute> {
            TODO()
        }
    }
}
