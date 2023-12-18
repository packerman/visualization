package common

import web.gl.Float32List
import web.gl.WebGL2RenderingContext
import web.gl.WebGLUniformLocation
import kotlin.reflect.KClass
import kotlin.reflect.cast

class UniformMap(private val uniforms: Map<String, Uniform<*>>) {

    @Suppress("unchecked_cast")
    operator fun <T : Any> get(name: String): T = uniforms.getValue(name).value as T

    operator fun <T : Any> get(name: String, aKClass: KClass<T>): T =
        aKClass.cast(uniforms.getValue(name).value)

    @Suppress("unchecked_cast")
    operator fun <T : Any> set(name: String, value: T) {
        val uniform = uniforms.getValue(name) as Uniform<T>
        uniform.value = value
    }

    fun asSequence() = uniforms.asSequence()
}

class UniformMapBuilder {

    private val uniforms = mutableMapOf<String, Uniform<*>>()

    fun uniform(name: String, value: Vector1) {
        uniforms[name] = Vector1Uniform(value)
    }

    fun uniform(name: String, value: Vector3) {
        uniforms[name] = Vector3Uniform(value)
    }

    fun uniform(name: String, value: Vector4) {
        uniforms[name] = Vector4Uniform(value)
    }

    fun uniform(name: String, value: Matrix4) {
        uniforms[name] = Matrix4Uniform(value)
    }

    operator fun Pair<String, Vector1>.unaryPlus() {
        uniform(first, second)
    }

    operator fun Pair<String, Vector3>.unaryPlus() {
        uniform(first, second)
    }

    operator fun Pair<String, Vector4>.unaryPlus() {
        uniform(first, second)
    }

    operator fun Pair<String, Matrix4>.unaryPlus() {
        uniform(first, second)
    }

    fun build() = UniformMap(uniforms.toMap())
}

fun uniformMap(block: UniformMapBuilder.() -> Unit) =
    UniformMapBuilder().apply { block() }.build()


interface Uniform<T : Any> {

    var value: T

    fun update(gl: WebGL2RenderingContext, location: WebGLUniformLocation?)
}

class Vector1Uniform(override var value: Vector1) : Uniform<Vector1> {

    override fun update(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform1f(location, value.x)
    }
}

class Vector3Uniform(override var value: Vector3) : Uniform<Vector3> {

    override fun update(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform3f(location, value.x, value.y, value.z)
    }
}

class Vector4Uniform(override var value: Vector4) : Uniform<Vector4> {

    override fun update(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        gl.uniform4f(location, value.x, value.y, value.z, value.w)
    }
}

class Matrix4Uniform(override var value: Matrix4) : Uniform<Matrix4> {

    private val list = Float32List(16)

    override fun update(gl: WebGL2RenderingContext, location: WebGLUniformLocation?) {
        value.copyTo(list)
        gl.uniformMatrix4fv(location, 0, list, 0, null)
    }

    companion object {
        fun Matrix4.copyTo(list: Float32List, offset: Int = 0) =
            forEachIndexed { i, f -> list[offset + i] = f }
    }
}