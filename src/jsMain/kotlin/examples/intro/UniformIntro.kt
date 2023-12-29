package examples.intro

import framework.core.*
import framework.core.Attribute.Companion.attribute
import framework.core.Uniform.Companion.uniform
import framework.math.Vector3
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGLUniformLocation
import web.gl.WebGLVertexArrayObject

typealias UniformLocation = Pair<Uniform<*>, WebGLUniformLocation?>

typealias UniformMap = Map<String, Uniform<*>>

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
            val id: WebGLVertexArrayObject, val count: Int
        )

        private data class Shape(
            val program: Program, val vertexArray: VertexArray, val uniforms: List<UniformLocation>, val mode: GLenum
        ) {
            fun render(gl: WebGL2RenderingContext) {
                program.use(gl)
                uploadData(gl, uniforms)
                gl.bindVertexArray(vertexArray.id)
                gl.drawArrays(mode, 0, vertexArray.count)
            }

            companion object {
                fun create(
                    program: Program, vertexArray: VertexArray, uniforms: UniformMap, mode: GLenum
                ): Shape {
                    return Shape(program, vertexArray, associateUniforms(program, uniforms), mode)
                }

                private fun uploadData(gl: WebGL2RenderingContext, uniformLocations: List<UniformLocation>) {
                    for ((uniform, location) in uniformLocations) {
                        uniform.uploadData(gl, location)
                    }
                }

                private fun associateUniforms(program: Program, uniforms: UniformMap): List<UniformLocation> =
                    uniforms.map { (name, uniform) -> uniform to program.getUniform(name).location }
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
                gl, program, mapOf(
                    "a_position" to attribute(
                        arrayOf(
                            0f, 0.2f, 0f, 0.2f, -0.2f, 0f, -0.2f, -0.2f, 0f
                        ), 3
                    )
                )
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
            gl: WebGL2RenderingContext, program: Program, attributes: Map<String, AttributeInitializer>
        ): VertexArray {
            val vertexArray = requireNotNull(gl.createVertexArray()) {
                "Cannot create vertex array object"
            }
            for ((name, initializer) in attributes) {
                gl.bindVertexArray(vertexArray)
                val attribute = initializer.initialize(gl)
                attribute.associateLocation(gl, program.getAttribute(name).location)
            }
            return VertexArray(
                vertexArray,
                attributes.asSequence().map { (_, attribute) -> attribute.count }.distinct().single()

            )
        }
    }
}
