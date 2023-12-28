package framework.core

import framework.math.Vector3
import js.core.toTypedArray
import web.gl.*
import web.gl.WebGL2RenderingContext.Companion.ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.FLOAT
import web.gl.WebGL2RenderingContext.Companion.STATIC_DRAW

class Attribute(
    private val buffer: WebGLBuffer,
    private val srcData: Float32List,
    private val size: GLint,
    private val type: GLenum,
) {

    fun uploadData(gl: WebGL2RenderingContext) {
        gl.bindBuffer(ARRAY_BUFFER, buffer)
        gl.bufferData(ARRAY_BUFFER, srcData, STATIC_DRAW)
    }

    fun associateLocation(gl: WebGL2RenderingContext, index: GLuint) {
        gl.bindBuffer(ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(index, size, type, 0, 0, 0)
        gl.enableVertexAttribArray(index)
    }

    val count: Int
        get() = srcData.length / size.toInt()

    companion object {
        fun attribute(array: Array<Array<Float>>): Supplier<Attribute> {
            require(array.isNotEmpty())
            require(array[0].isNotEmpty())
            val srcData = Float32List(array.asSequence()
                .flatMap { it.asSequence() }
                .toTypedArray())
            return attribute(srcData, array[0].size)
        }

        fun attribute(array: Array<Float>, size: Int): Supplier<Attribute> {
            require(array.isNotEmpty())
            require(array.size % size == 0)
            val srcData = Float32List(array)
            return attribute(srcData, size)
        }

        fun attribute(array: Array<Vector3>): Supplier<Attribute> {
            require(array.isNotEmpty())
            val srcData = Float32List(array.asSequence()
                .flatMap { it.asSequence() }
                .toTypedArray())
            return attribute(srcData, 3)
        }

        private fun attribute(srcData: Float32List, size: Int): Supplier<Attribute> = { gl ->
            val buffer = requireNotNull(gl.createBuffer())
            val attribute = Attribute(buffer, srcData, size, FLOAT)
            attribute.uploadData(gl)
            attribute
        }
    }
}
