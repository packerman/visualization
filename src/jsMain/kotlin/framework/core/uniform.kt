package framework.core

import framework.core.Uniform.Companion.uniform
import framework.math.Matrix4
import framework.math.Vector3
import web.gl.Float32List
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.BOOL
import web.gl.WebGL2RenderingContext.Companion.FLOAT_MAT4
import web.gl.WebGL2RenderingContext.Companion.FLOAT_VEC3
import web.gl.WebGLUniformLocation

sealed class Uniform<T>(var data: T, val dataType: GLenum) {

    abstract fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?)

    override fun toString(): String {
        return "Uniform(data=$data, dataType=$dataType)"
    }

    companion object {
        fun uniform(data: Boolean): Uniform<Boolean> = BooleanUniform(data)

        fun uniform(data: Vector3): Uniform<Vector3> = Vector3Uniform(data)

        fun uniform(data: Matrix4): Uniform<Matrix4> = Matrix4Uniform(data)
    }
}

private class BooleanUniform(data: Boolean) : Uniform<Boolean>(data, BOOL) {
    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform1i(location, if (data) 1f else 0f)
    }
}

private class Vector3Uniform(data: Vector3) : Uniform<Vector3>(data, FLOAT_VEC3) {
    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform3f(location, data.x, data.y, data.z)
    }
}

private class Matrix4Uniform(data: Matrix4) : Uniform<Matrix4>(data, FLOAT_MAT4) {

    private val list = Float32List(16)

    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        data.copyTo(list)
        gl.uniformMatrix4fv(location, 0, list, null, null)
    }

    companion object {
        private fun Matrix4.copyTo(destination: Float32List) {
            forEachIndexed { i, value ->
                destination[i] = value
            }
        }
    }
}

class UniformMapBuilder {
    private val uniforms = mutableMapOf<String, Uniform<*>>()

    fun uniform(name: String, value: Boolean) {
        uniforms[name] = uniform(value)
    }

    fun uniform(name: String, value: Vector3) {
        uniforms[name] = uniform(value)
    }

    fun uniform(name: String, value: Matrix4) {
        uniforms[name] = uniform(value)
    }

    fun build() = uniforms.toMap()
}

fun uniformMap(block: (UniformMapBuilder).() -> Unit): Map<String, Uniform<*>> =
    UniformMapBuilder().apply(block).build()
