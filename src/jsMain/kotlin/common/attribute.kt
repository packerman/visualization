package common

import js.typedarrays.Float32Array
import web.gl.WebGL2RenderingContext
import web.gl.WebGLBuffer

class Attribute(private val arrayBuffer: WebGLBuffer, private val length: Int) {

    fun getCount(size: Int): Int {
        require(length % size == 0)
        return length / size
    }

    companion object {
        fun attribute(floatArray: Array<Float>): Supplier<Attribute> {
            return { gl ->
                val buffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
                gl.bindBuffer(WebGL2RenderingContext.ARRAY_BUFFER, buffer)
                gl.bufferData(
                    WebGL2RenderingContext.ARRAY_BUFFER,
                    Float32Array(floatArray),
                    WebGL2RenderingContext.STATIC_DRAW
                )
                Attribute(buffer, floatArray.size)
            }
        }
    }
}
