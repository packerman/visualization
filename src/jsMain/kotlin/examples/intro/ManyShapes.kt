package examples.intro

import framework.core.Application
import framework.core.Attribute.Companion.attribute
import framework.core.Initializer
import framework.core.Program
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.LINE_LOOP
import web.gl.WebGLVertexArrayObject

@Suppress("unused")
class ManyShapes(
    private val program: Program,
    private val shapes: List<Shape>
) : Application {
    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT)
        program.use(gl)
        for (shape in shapes) {
            shape.render(gl)
        }
    }

    companion object : Initializer<ManyShapes> {
        data class Shape(
            val vertexArray: WebGLVertexArrayObject,
            val vertexCount: Int
        ) {
            fun render(gl: WebGL2RenderingContext) {
                gl.bindVertexArray(vertexArray)
                gl.drawArrays(LINE_LOOP, 0, vertexCount)
            }
        }

        override fun initialize(gl: WebGL2RenderingContext): ManyShapes {
            gl.clearColor(0.0, 0.0, 0.0, 1.0)
            val program = Program.build(
                gl,
                """
                    in vec3 a_position;
                    
                    void main() {
                        gl_Position = vec4(a_position, 1.0);
                    }
                """,
                """
                    out vec4 fragColor;
                    
                    void main() {
                        fragColor = vec4(1.0, 1.0, 0.0, 1.0);
                    }
                """
            )
            program.use(gl)

            fun setupVertexArray(gl: WebGL2RenderingContext, data: Array<Array<Float>>): Shape {
                val vao = requireNotNull(gl.createVertexArray()) {
                    "Cannot create vertex array object"
                }
                gl.bindVertexArray(vao)
                val vertexCount = data.size
                val attribute = attribute(data).initialize(gl)
                attribute.associateLocation(gl, program.getAttribute("a_position").location)
                return Shape(vao, vertexCount)
            }

            val vao = requireNotNull(gl.createVertexArray()) {
                "Cannot create vertex array object"
            }
            gl.bindVertexArray(vao)
            val shapes = listOf(
                setupVertexArray(
                    gl,
                    arrayOf(
                        arrayOf(-0.5f, 0.8f, 0f),
                        arrayOf(-0.2f, 0.2f, 0f),
                        arrayOf(-0.8f, 0.2f, 0f)
                    )
                ),
                setupVertexArray(
                    gl,
                    arrayOf(
                        arrayOf(0.8f, 0.8f, 0f),
                        arrayOf(0.8f, 0.2f, 0f),
                        arrayOf(0.2f, 0.2f, 0f),
                        arrayOf(0.2f, 0.8f, 0f)
                    )
                )
            )
            return ManyShapes(program, shapes)
        }
    }
}
