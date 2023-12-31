package framework.core

import js.typedarrays.Uint16Array
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.STATIC_DRAW
import web.gl.WebGL2RenderingContext.Companion.UNSIGNED_SHORT
import web.gl.WebGLBuffer

class Index(val buffer: WebGLBuffer, val count: Int, val type: GLenum) {

    fun bind(gl: WebGL2RenderingContext) {
        gl.bindBuffer(ELEMENT_ARRAY_BUFFER, buffer)
    }

    fun draw(gl: WebGL2RenderingContext, mode: GLenum) {
        gl.drawElements(mode, count, type, 0)
    }

    companion object {
        operator fun invoke(gl: WebGL2RenderingContext, array: Array<Short>): Index {
            val buffer = requireNotNull(gl.createBuffer())
            gl.bindBuffer(ELEMENT_ARRAY_BUFFER, buffer)
            gl.bufferData(ELEMENT_ARRAY_BUFFER, Uint16Array(array), STATIC_DRAW)
            return Index(buffer, array.size, UNSIGNED_SHORT)
        }
    }
}
