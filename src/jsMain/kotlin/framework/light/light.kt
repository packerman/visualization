package framework.light

import framework.core.*
import framework.light.LightType.*
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

enum class LightType {
    AMBIENT,
    DIRECTIONAL,
    POINT;

    companion object {
        fun getGlslDeclarations(): String {
            return entries.asSequence()
                .map { "const int ${it.name} = ${it.ordinal};" }
                .joinToString("\n")
        }
    }
}

class Light private constructor(
    private val type: LightType? = null,
    private val color: Vector3 = Vector3(1f, 1f, 1f),
    private val attenuation: Vector3 = Vector3(1f, 0f, 0f)
) :
    Node by NodeImpl(),
    UniformUpdater {
    override fun updateData(gl: WebGL2RenderingContext, name: String, program: Program) {
        IntUniform.uploadData(gl, program.getUniform(name, "lightType")?.location, type?.ordinal ?: -1)
        Vector3Uniform.uploadData(gl, program.getUniform(name, "color")?.location, color)
        when (type) {
            DIRECTIONAL -> {
                Vector3Uniform.uploadData(gl, program.getUniform(name, "direction")?.location, direction)
                console.log("Set $direction to $name ${program.getUniform(name, "direction")?.location}")
            }

            POINT -> {
                Vector3Uniform.uploadData(gl, program.getUniform(name, "position")?.location, position)
                Vector3Uniform.uploadData(gl, program.getUniform(name, "attenuation")?.location, attenuation)
            }

            else -> {}
        }
    }

    companion object {
        fun ambient(color: Vector3 = Vector3(1f, 1f, 1f)): Light = Light(AMBIENT, color)

        fun directional(color: Vector3 = Vector3(1f, 1f, 1f), direction: Vector3 = Vector3(0f, -1f, 0f)): Light =
            Light(DIRECTIONAL, color).apply {
                this.direction = direction
            }

        fun point(
            color: Vector3 = Vector3(1f, 1f, 1f),
            position: Vector3 = Vector3(0f, 0f, 0f),
            attenuation: Vector3 = Vector3(1f, 0f, 0.1f)
        ): Light = Light(POINT, color, attenuation).apply {
            this.position = position
        }
    }
}
