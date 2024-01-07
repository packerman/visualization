package examples.intro

import framework.core.*
import framework.core.BasicUniform.Companion.uniform
import framework.geometry.Geometry
import framework.geometry.Geometry.Companion.POSITION
import framework.geometry.geometry
import framework.math.Vector3
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGLVertexArrayObject
import kotlin.js.Date
import kotlin.math.sin

@Suppress("unused")
class AnimationColor private constructor(
    private val program: Program,
    private val shapes: List<Shape>,
    private val translation: BasicUniform<Vector3>,
    private val baseColor: BasicUniform<Vector3>

) : Application {

    private val frequencies = Vector3(3f, 0f, 0f)
    private val phases = Vector3(0f, 0f, 0f)

    override fun start(gl: WebGL2RenderingContext) {
        gui("GUI") {
            fun colorFolder(name: String, i: Int) = folder(name) {
                number("Frequency", frequencies[i], 0f..15f, 0.1f) {
                    frequencies[i] = it
                }
                number("Phase", phases[i], 0f..6.28f, 0.01f) {
                    phases[i] = it
                }
            }
            colorFolder("Red", 0)
            colorFolder("Green", 1)
            colorFolder("Blue", 2)
            folder("Transform") {
                vector("Translation", translation.data, -1f..1f, 0.01f) {
                    it.copyTo(translation.data)
                }
            }
        }
    }

    override fun update(elapsed: Double, keyState: KeyState) {
        val time = Date.now().toFloat() / 1000f
        for (i in 0..2) {
            baseColor.data[i] = (sin(frequencies[i] * time + phases[i]) + 1) / 2
        }
    }

    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT)
        for (shape in shapes) {
            shape.render(gl)
        }
    }

    companion object : Initializer<AnimationColor> {
        private data class VertexArray(
            val id: WebGLVertexArrayObject,
            val geometry: Geometry
        )

        private data class Shape(
            val program: Program,
            val vertexArray: VertexArray,
            val uniforms: Map<String, BasicUniform<*>>,
            val mode: GLenum
        ) {
            fun render(gl: WebGL2RenderingContext) {
                program.use(gl)
                uploadData(gl, program, uniforms)
                gl.bindVertexArray(vertexArray.id)
                gl.drawArrays(mode, 0, vertexArray.geometry.vertexCount)
            }
        }

        override fun initialize(gl: WebGL2RenderingContext): AnimationColor {
            gl.clearColor(0.9, 0.9, 0.9, 1.0)
            gl.lineWidth(5f)
            val program = Program.build(
                gl,
                """
                    in vec3 a_position;
                    
                    uniform vec3 u_Translation;
                    
                    void main() {
                        gl_Position = vec4(a_position + u_Translation, 1.0);
                    }
                """,
                """
                    uniform vec3 u_BaseColor;
                    out vec4 fragColor;
                    
                    void main() {
                        fragColor = vec4(u_BaseColor, 1.0);
                    }
                """
            )
            program.use(gl)
            val vertexArray = setupVertexArray(
                gl, program, geometry(gl) {
                    attribute(POSITION, arrayOf(0f, 0.2f, 0f, 0.2f, -0.2f, 0f, -0.2f, -0.2f, 0f), 3)
                }
            )
            val baseColor = uniform(Vector3(1f, 0f, 0f))
            val translation = uniform(Vector3(-0.5f, 0f, 0f))
            val shapes = listOf(
                Shape(
                    program, vertexArray, mapOf(
                        "u_Translation" to translation,
                        "u_BaseColor" to baseColor
                    ), TRIANGLES
                )
            )

            return AnimationColor(program, shapes, translation, baseColor)
        }

        private fun setupVertexArray(
            gl: WebGL2RenderingContext,
            program: Program,
            geometry: Geometry
        ): VertexArray {
            val vertexArray = requireNotNull(gl.createVertexArray()) {
                "Cannot create vertex array object"
            }
            geometry.buildVertexArray(gl, program)
            return VertexArray(vertexArray, geometry)
        }

        private fun uploadData(gl: WebGL2RenderingContext, program: Program, uniforms: Map<String, BasicUniform<*>>) {
            for ((name, uniform) in uniforms) {
                program.getUniform(name)?.let { active ->
                    uniform.uploadData(gl, active.location)
                }
            }
        }
    }
}
