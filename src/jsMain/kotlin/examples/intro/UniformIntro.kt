package examples.intro

import framework.core.Application
import framework.core.Initializer
import framework.core.Program
import framework.core.Uniform
import framework.core.Uniform.Companion.uniform
import framework.geometry.Geometry
import framework.geometry.geometry
import framework.math.Vector3
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGLUniformLocation
import web.gl.WebGLVertexArrayObject

@Suppress("unused")
class UniformIntro private constructor(
    private val program: Program, private val shapes: List<Shape>
) : Application {

    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT)
        for (shape in shapes) {
            shape.render(gl)
        }
    }

    companion object : Initializer<UniformIntro> {

        private data class VertexArray(
            val id: WebGLVertexArrayObject,
            val geometry: Geometry
        )

        private data class Shape(
            val program: Program,
            val vertexArray: VertexArray,
            val uniforms: List<Pair<Uniform<*>, WebGLUniformLocation?>>,
            val mode: GLenum
        ) {
            fun render(gl: WebGL2RenderingContext) {
                program.use(gl)
                uploadData(gl, uniforms)
                gl.bindVertexArray(vertexArray.id)
                gl.drawArrays(mode, 0, vertexArray.geometry.vertexCount)
            }

            companion object {
                fun create(
                    program: Program,
                    vertexArray: VertexArray,
                    uniforms: Map<String, Uniform<*>>,
                    mode: GLenum
                ): Shape {
                    return Shape(program, vertexArray, associateUniforms(program, uniforms), mode)
                }

                private fun uploadData(
                    gl: WebGL2RenderingContext,
                    uniformLocations: List<Pair<Uniform<*>, WebGLUniformLocation?>>
                ) {
                    for ((uniform, location) in uniformLocations) {
                        uniform.uploadData(gl, location)
                    }
                }

                private fun associateUniforms(
                    program: Program,
                    uniforms: Map<String, Uniform<*>>
                ): List<Pair<Uniform<*>, WebGLUniformLocation?>> =
                    uniforms.map { (name, uniform) -> uniform to program.getUniform(name)!!.location }
            }
        }

        override fun initialize(gl: WebGL2RenderingContext): UniformIntro {
            gl.clearColor(0.9, 0.9, 0.9, 1.0)
            gl.lineWidth(5f)
            val program = Program.build(
                gl, """
                    in vec3 a_position;
                    
                    uniform vec3 u_Translation;
                    
                    void main() {
                        gl_Position = vec4(a_position + u_Translation, 1.0);
                    }
                """, """
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
                    attribute(
                        "a_position",
                        arrayOf(
                            0f, 0.2f, 0f, 0.2f, -0.2f, 0f, -0.2f, -0.2f, 0f
                        ), 3
                    )
                }
            )
            val shapes = listOf(
                Shape.create(
                    program, vertexArray, mapOf(
                        "u_Translation" to uniform(Vector3(-0.5f, 0f, 0f)),
                        "u_BaseColor" to uniform(Vector3(1f, 0f, 0f))
                    ), TRIANGLES
                ), Shape.create(
                    program, vertexArray, mapOf(
                        "u_Translation" to uniform(Vector3(0.5f, 0f, 0f)), "u_BaseColor" to uniform(Vector3(0f, 0f, 1f))
                    ), TRIANGLES
                )
            )
            return UniformIntro(program, shapes)
        }

        private fun setupVertexArray(
            gl: WebGL2RenderingContext,
            program: Program,
            geometry: Geometry
        ): VertexArray {
            val vertexArray = requireNotNull(gl.createVertexArray()) {
                "Cannot create vertex array object"
            }
            geometry.buildArray(gl, program)
            return VertexArray(vertexArray, geometry)
        }
    }
}
