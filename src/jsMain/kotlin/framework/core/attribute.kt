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

    val count: Int
        get() = srcData.length / size.toInt()

    fun uploadData(gl: WebGL2RenderingContext) {
        gl.bindBuffer(ARRAY_BUFFER, buffer)
        gl.bufferData(ARRAY_BUFFER, srcData, STATIC_DRAW)
    }

    fun associateLocation(gl: WebGL2RenderingContext, index: GLuint) {
        gl.bindBuffer(ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(index, size, type, 0, 0, 0)
        gl.enableVertexAttribArray(index)
    }

    companion object {

        operator fun invoke(gl: WebGL2RenderingContext, array: Array<Array<Float>>): Attribute {
            require(array.isNotEmpty())
            require(array[0].isNotEmpty())
            val srcData = Float32List(array.asSequence()
                .flatMap { it.asSequence() }
                .toTypedArray())
            return Attribute(gl, srcData, array[0].size)
        }

        operator fun invoke(gl: WebGL2RenderingContext, list: List<Array<Float>>): Attribute {
            require(list.isNotEmpty())
            require(list[0].isNotEmpty())
            val srcData = Float32List(list.asSequence()
                .flatMap { it.asSequence() }
                .toTypedArray())
            return Attribute(gl, srcData, list[0].size)
        }

        operator fun invoke(gl: WebGL2RenderingContext, array: Array<Float>, size: Int): Attribute {
            require(array.isNotEmpty())
            require(array.size % size == 0)
            val srcData = Float32List(array)
            return Attribute(gl, srcData, size)
        }

        operator fun invoke(gl: WebGL2RenderingContext, array: Array<Vector3>): Attribute {
            require(array.isNotEmpty())
            val srcData = Float32List(array.asSequence()
                .flatMap { it.asSequence() }
                .toTypedArray())
            return Attribute(gl, srcData, 3)
        }

        operator fun invoke(gl: WebGL2RenderingContext, srcData: Float32List, size: Int): Attribute {
            val buffer = requireNotNull(gl.createBuffer())
            val attribute = Attribute(buffer, srcData, size, FLOAT)
            attribute.uploadData(gl)
            return attribute
        }
    }
}
