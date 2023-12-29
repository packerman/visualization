package examples.intro

import framework.core.Application
import framework.core.Initializer
import framework.core.Program
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.POINTS
import web.gl.WebGLVertexArrayObject

@Suppress("unused")
class DrawPoint(
    private val program: Program,
    private val vao: WebGLVertexArrayObject
) : Application {

    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT)
        program.use(gl)
        gl.bindVertexArray(vao)
        gl.drawArrays(POINTS, 0, 1)
    }

    companion object : Initializer<DrawPoint> {
        override fun initialize(gl: WebGL2RenderingContext): DrawPoint {
            gl.clearColor(0.0, 0.0, 0.0, 1.0)
            val program = Program.build(
                gl,
                """
                    void main() {
                        gl_PointSize = 10.0;
                        gl_Position = vec4(0.0, 0.0, 0.0, 1.0);
                    }
                """,
                """
                    out vec4 fragColor;
                    void main() {
                        fragColor = vec4(1.0, 1.0, 0.0, 1.0);
                    }
                """
            )
            val vao = requireNotNull(gl.createVertexArray()) {
                "Cannot create vertex array object"
            }
            return DrawPoint(program, vao)
        }
    }
}
