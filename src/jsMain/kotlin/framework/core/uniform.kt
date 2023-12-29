package framework.core

import framework.math.Vector3
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGLUniformLocation

sealed interface Uniform {
    fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?)

    val dataType: GLenum

    companion object {
        fun uniform(data: Vector3): Uniform = Vector3Uniform(data)
    }
}

private class Vector3Uniform(private val data: Vector3) : Uniform {
    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform3f(location, data.x, data.y, data.z)
    }

    override val dataType: GLenum
        get() = WebGL2RenderingContext.FLOAT_VEC3
}
