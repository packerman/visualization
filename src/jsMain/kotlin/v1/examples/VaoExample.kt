package v1.examples

import framework.core.Program
import js.typedarrays.Float32Array
import js.typedarrays.Uint16Array
import v1.common.Application
import web.gl.GLint
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.COLOR_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.STATIC_DRAW
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGLVertexArrayObject
import web.html.HTMLCanvasElement

@Suppress("unused")
class VaoExample(
    private val vao: WebGLVertexArrayObject,
    private val positionLocation: GLint,
    private val count: Int
) : Application {
    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(COLOR_BUFFER_BIT.toInt() or DEPTH_BUFFER_BIT.toInt())
        gl.viewport(0, 0, (gl.canvas as HTMLCanvasElement).width, (gl.canvas as HTMLCanvasElement).height)

        gl.bindVertexArray(vao)

        gl.drawElements(TRIANGLES, count, WebGL2RenderingContext.UNSIGNED_SHORT, 0)

        gl.bindVertexArray(null)
    }

    companion object {
        fun initialize(gl: WebGL2RenderingContext): VaoExample {
            val vertices = arrayOf(
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f
            )
            val indices: Array<Short> = arrayOf(0, 1, 2, 0, 2, 3)

            val vao = requireNotNull(gl.createVertexArray()) { "Cannot create VAO" }
            gl.bindVertexArray(vao)

            val buffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
            gl.bindBuffer(ARRAY_BUFFER, buffer)
            gl.bufferData(ARRAY_BUFFER, Float32Array(vertices), STATIC_DRAW)

            val indexBuffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
            gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indexBuffer)
            gl.bufferData(ELEMENT_ARRAY_BUFFER, Uint16Array(indices), STATIC_DRAW)

            val program = Program.build(gl, vertexShaderSource, fragmentShaderSource)
            program.use(gl)

            val position = program.getAttribute("aVertexPosition")

            gl.vertexAttribPointer(position.location, 3, WebGL2RenderingContext.FLOAT, 0, 0, 0)
            gl.enableVertexAttribArray(position.location)

            gl.bindVertexArray(null)
            gl.bindBuffer(ARRAY_BUFFER, null)
            gl.bindBuffer(ELEMENT_ARRAY_BUFFER, null)

            return VaoExample(vao, position.location, indices.size)
        }

        private val vertexShaderSource = """
            #version 300 es
            precision mediump float;

            // Supplied vertex position attribute
            in vec3 aVertexPosition;

            void main(void) {
                gl_Position = vec4(aVertexPosition, 1.0);
            }
            """.trimIndent()

        private val fragmentShaderSource = """
            #version 300 es
            precision mediump float;

            out vec4 fragColor;

            void main(void) {
                fragColor = vec4(0.0, 0.0, 1.0, 1.0);
            }
            """.trimIndent()
    }
}
