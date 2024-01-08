package framework.material

import framework.core.BasicUniform
import framework.core.BasicUniform.Companion.uniform
import framework.core.IntUniform
import framework.core.Program
import framework.core.UniformUpdater
import framework.math.Matrix4
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.CULL_FACE

interface Material {
    val program: Program
    fun <T> getUniform(name: String): T
    fun <T> setUniform(name: String, data: T)
    fun uploadData(gl: WebGL2RenderingContext)
    fun updateRenderSettings(gl: WebGL2RenderingContext)
    fun hasUniform(name: String): Boolean
    fun <U : UniformUpdater> updateArray(gl: WebGL2RenderingContext, array: Array<U>, name: String, countName: String)
}

class MaterialImpl private constructor(
    override val program: Program,
    private val uniforms: Map<String, UniformUpdater> = mapOf(),
    private val doubleSided: Boolean = false
) : Material {

    @Suppress("unchecked_cast")
    override fun <T> getUniform(name: String): T = (uniforms.getValue(name) as BasicUniform<T>).data

    @Suppress("unchecked_cast")
    override fun <T> setUniform(name: String, data: T) {
        uniforms[name]?.let { uniform ->
            (uniform as BasicUniform<T>).data = data
        }
    }

    override fun uploadData(gl: WebGL2RenderingContext) {
        for ((name, uniform) in uniforms) {
            uniform.updateData(gl, name, program)
        }
    }

    override fun updateRenderSettings(gl: WebGL2RenderingContext) {
        if (doubleSided) {
            gl.disable(CULL_FACE)
        } else {
            gl.enable(CULL_FACE)
        }
    }

    override fun hasUniform(name: String): Boolean = program.hasUniform(name)

    override fun <U : UniformUpdater> updateArray(
        gl: WebGL2RenderingContext,
        array: Array<U>,
        name: String,
        countName: String
    ) {
        fun indexName(i: Int) = "$name[$i]"
        IntUniform.uploadData(gl, program.getUniform(countName)?.location, array.size)
        for (i in array.indices) {
            array[i].updateData(gl, indexName(i), program)
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
            uniforms: Map<String, BasicUniform<*>> = mapOf(),
            doubleSided: Boolean = false
        ): Material {
            val program = Program.build(gl, vertexShaderSource, fragmentShaderSource)
            val allUniforms: MutableMap<String, BasicUniform<*>> = mutableMapOf(
                MODEL_MATRIX to uniform(Matrix4()),
                VIEW_MATRIX to uniform(Matrix4()),
                PROJECTION_MATRIX to uniform(Matrix4())
            )
            allUniforms.putAll(uniforms)
            return MaterialImpl(program, allUniforms, doubleSided)
        }
    }
}
