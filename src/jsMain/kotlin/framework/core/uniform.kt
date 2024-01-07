package framework.core

import framework.core.BasicUniform.Companion.uniform
import framework.math.Matrix4
import framework.math.Vector2
import framework.math.Vector3
import web.gl.Float32List
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.BOOL
import web.gl.WebGL2RenderingContext.Companion.FLOAT_MAT4
import web.gl.WebGL2RenderingContext.Companion.FLOAT_VEC2
import web.gl.WebGL2RenderingContext.Companion.FLOAT_VEC3
import web.gl.WebGL2RenderingContext.Companion.SAMPLER_2D
import web.gl.WebGL2RenderingContext.Companion.TEXTURE_2D
import web.gl.WebGLUniformLocation

interface Uniform {
    fun updateData(gl: WebGL2RenderingContext, name: String, program: Program)
}

sealed class BasicUniform<T>(var data: T, val dataType: GLenum) : Uniform {

    override fun updateData(gl: WebGL2RenderingContext, name: String, program: Program) {
        program.getUniform(name)?.let { active ->
            uploadData(gl, active.location)
        }
    }

    abstract fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?)

    override fun toString(): String = "Uniform(data=$data, dataType=$dataType)"

    companion object {
        fun uniform(data: Boolean): BasicUniform<Boolean> = BooleanUniform(data)

        fun uniform(data: Vector2): BasicUniform<Vector2> = Vector2Uniform(data)

        fun uniform(data: Vector3): BasicUniform<Vector3> = Vector3Uniform(data)

        fun uniform(data: Matrix4): BasicUniform<Matrix4> = Matrix4Uniform(data)

        fun uniform(data: Sampler2D): BasicUniform<Sampler2D> = Sampler2DUniform(data)
    }
}

private class BooleanUniform(data: Boolean) : BasicUniform<Boolean>(data, BOOL) {
    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform1i(location, if (data) 1f else 0f)
    }
}

private class Vector2Uniform(data: Vector2) : BasicUniform<Vector2>(data, FLOAT_VEC2) {
    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform2f(location, data.x, data.y)
    }
}

private class Vector3Uniform(data: Vector3) : BasicUniform<Vector3>(data, FLOAT_VEC3) {
    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform3f(location, data.x, data.y, data.z)
    }
}

private class Matrix4Uniform(data: Matrix4) : BasicUniform<Matrix4>(data, FLOAT_MAT4) {

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

private class Sampler2DUniform(data: Sampler2D) : BasicUniform<Sampler2D>(data, SAMPLER_2D) {
    override fun uploadData(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.activeTexture(WebGL2RenderingContext.TEXTURE0.toInt() + data.textureUnit)
        gl.bindTexture(TEXTURE_2D, data.texture)
        gl.uniform1i(location, data.textureUnit)
    }

}

class UniformMapBuilder {
    private val uniforms = mutableMapOf<String, BasicUniform<*>>()

    fun uniform(name: String, value: Boolean) {
        uniforms[name] = uniform(value)
    }

    fun uniform(name: String, value: Vector2) {
        uniforms[name] = uniform(value)
    }

    fun uniform(name: String, value: Vector3) {
        uniforms[name] = uniform(value)
    }

    fun uniform(name: String, value: Matrix4) {
        uniforms[name] = uniform(value)
    }

    fun uniform(name: String, value: Sampler2D) {
        uniforms[name] = uniform(value)
    }

    fun build() = uniforms.toMap()
}

fun uniformMap(block: (UniformMapBuilder).() -> Unit): Map<String, BasicUniform<*>> =
    UniformMapBuilder().apply(block).build()
