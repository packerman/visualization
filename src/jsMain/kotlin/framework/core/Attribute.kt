package framework.core

import framework.math.Vector3
import js.core.toTypedArray
import web.gl.*
import web.gl.WebGL2RenderingContext.Companion.ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.FLOAT
import web.gl.WebGL2RenderingContext.Companion.STATIC_DRAW

class AttributeInitializer(private val srcData: Float32List, private val size: Int) : Initializer<Attribute> {

    override fun initialize(gl: WebGL2RenderingContext): Attribute {
        val buffer = requireNotNull(gl.createBuffer())
        val attribute = Attribute(buffer, srcData, size, FLOAT)
        attribute.uploadData(gl)
        return attribute
    }

    val count: Int
        get() = srcData.length / size
}

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
        fun attribute(array: Array<Array<Float>>): AttributeInitializer {
            require(array.isNotEmpty())
            require(array[0].isNotEmpty())
            val srcData = Float32List(array.asSequence()
                .flatMap { it.asSequence() }
                .toTypedArray())
            return AttributeInitializer(srcData, array[0].size)
        }

        fun attribute(array: Array<Float>, size: Int): AttributeInitializer {
            require(array.isNotEmpty())
            require(array.size % size == 0)
            val srcData = Float32List(array)
            return AttributeInitializer(srcData, size)
        }

        fun attribute(array: Array<Vector3>): AttributeInitializer {
            require(array.isNotEmpty())
            val srcData = Float32List(array.asSequence()
                .flatMap { it.asSequence() }
                .toTypedArray())
            return AttributeInitializer(srcData, 3)
        }
    }
}
