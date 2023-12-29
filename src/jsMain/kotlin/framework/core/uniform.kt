package framework.core

import framework.math.Vector3
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.FLOAT_VEC3
import web.gl.WebGLUniformLocation

abstract class Uniform<T>(val data: T, val dataType: GLenum) {
    abstract fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?)

    companion object {
        fun uniform(data: Vector3): Uniform<Vector3> = object : Uniform<Vector3>(data, FLOAT_VEC3) {
            override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
                gl.uniform3f(location, data.x, data.y, data.z)
            }
        }
    }
}
