package framework.material

import framework.core.Program
import framework.core.Uniform
import framework.core.Uniform.Companion.uniform
import framework.math.Matrix4
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.CULL_FACE

interface Material {
    val program: Program
    fun <T> getUniform(name: String): T
    fun <T> setUniform(name: String, data: T)
    fun uploadData(gl: WebGL2RenderingContext)
    fun updateRenderSettings(gl: WebGL2RenderingContext)
}

class MaterialImpl private constructor(
    override val program: Program,
    private val uniforms: Map<String, Uniform<*>> = mapOf(),
    private val doubleSided: Boolean = false
) : Material {

    @Suppress("unchecked_cast")
    override fun <T> getUniform(name: String): T = (uniforms.getValue(name) as Uniform<T>).data

    @Suppress("unchecked_cast")
    override fun <T> setUniform(name: String, data: T) {
        uniforms[name]?.let { uniform ->
            (uniform as Uniform<T>).data = data
        }
    }

    override fun uploadData(gl: WebGL2RenderingContext) {
        for ((name, uniform) in uniforms) {
            val location = program.getUniform(name).location
            uniform.uploadData(gl, location)
        }
    }

    override fun updateRenderSettings(gl: WebGL2RenderingContext) {
        if (doubleSided) {
            gl.disable(CULL_FACE)
        } else {
            gl.enable(CULL_FACE)
        }
    }

    companion object {
        const val MODEL_MATRIX = "u_ModelMatrix"
        const val VIEW_MATRIX = "u_ViewMatrix"
        const val PROJECTION_MATRIX = "u_ProjectionMatrix"

        operator fun invoke(
            gl: WebGL2RenderingContext,
            vertexShaderSource: String,
            fragmentShaderSource: String,
            uniforms: Map<String, Uniform<*>> = mapOf(),
            doubleSided: Boolean = false
        ): Material {
            val program = Program.build(gl, vertexShaderSource, fragmentShaderSource)
            val allUniforms: MutableMap<String, Uniform<*>> = mutableMapOf(
                MODEL_MATRIX to uniform(Matrix4()),
                VIEW_MATRIX to uniform(Matrix4()),
                PROJECTION_MATRIX to uniform(Matrix4())
            )
            allUniforms.putAll(uniforms)
            return MaterialImpl(program, allUniforms, doubleSided)
        }
    }
}
