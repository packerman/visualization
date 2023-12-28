package examples.intro

import framework.core.Attribute.Companion.attribute
import framework.core.Base
import framework.core.Initializer
import framework.core.Program
import web.gl.WebGL2RenderingContext
import web.gl.WebGLVertexArrayObject

class OutlinedHexagon(
    private val program: Program,
    private val vao: WebGLVertexArrayObject,
    private val vertexCount: Int
) : Base {
    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT)
        program.use(gl)
        gl.bindVertexArray(vao)
        gl.drawArrays(WebGL2RenderingContext.LINE_LOOP, 0, vertexCount)
    }

    companion object : Initializer<OutlinedHexagon> {
        override fun initialize(gl: WebGL2RenderingContext): OutlinedHexagon {
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
            val vao = requireNotNull(gl.createVertexArray()) {
                "Cannot create vertex array object"
            }
            gl.bindVertexArray(vao)
            val positionData = arrayOf(
                arrayOf(0.8f, 0f, 0f), arrayOf(0.4f, 0.6f, 0f),
                arrayOf(-0.4f, 0.6f, 0f), arrayOf(-0.8f, 0f, 0f),
                arrayOf(-0.4f, -0.6f, 0f), arrayOf(0.4f, -0.6f, 0f)
            )
            val vertexCount = positionData.size
            val positionAttribute = attribute(positionData)(gl)
            positionAttribute.associateLocation(gl, program.attributes.getValue("a_position").location)
            return OutlinedHexagon(program, vao, vertexCount)
        }
    }
}
