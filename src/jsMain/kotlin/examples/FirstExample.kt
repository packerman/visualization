package examples

import common.Application
import common.Program
import js.typedarrays.Float32Array
import js.typedarrays.Uint16Array
import web.gl.GLint
import web.gl.WebGL2RenderingContext
import web.gl.WebGLBuffer
import web.html.HTMLCanvasElement

@Suppress("unused")
class FirstExample(
    private val buffer: WebGLBuffer,
    private val indexBuffer: WebGLBuffer,
    private val positionLocation: GLint,
    private val count: Int
): Application {
    override fun draw(gl: WebGL2RenderingContext) {
        gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT.toInt() or WebGL2RenderingContext.DEPTH_BUFFER_BIT.toInt())
        gl.viewport(0, 0, (gl.canvas as HTMLCanvasElement).width, (gl.canvas as HTMLCanvasElement).height)

        gl.bindBuffer(WebGL2RenderingContext.ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(positionLocation, 3, WebGL2RenderingContext.FLOAT, 0, 0, 0)
        gl.enableVertexAttribArray(positionLocation)

        gl.bindBuffer(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, indexBuffer)

        gl.drawElements(WebGL2RenderingContext.TRIANGLES, count, WebGL2RenderingContext.UNSIGNED_SHORT, 0)
    }

    companion object {
        fun initialize(gl: WebGL2RenderingContext): FirstExample {
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
            gl.bindBuffer(WebGL2RenderingContext.ARRAY_BUFFER, buffer)
            gl.bufferData(
                WebGL2RenderingContext.ARRAY_BUFFER, Float32Array(vertices),
                WebGL2RenderingContext.STATIC_DRAW
            )

            val indexBuffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
            gl.bindBuffer(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, indexBuffer)
            gl.bufferData(
                WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, Uint16Array(indices),
                WebGL2RenderingContext.STATIC_DRAW
            )

            gl.bindVertexArray(null)
            gl.bindBuffer(WebGL2RenderingContext.ARRAY_BUFFER, null)
            gl.bindBuffer(WebGL2RenderingContext.ELEMENT_ARRAY_BUFFER, null)

            val program = Program.build(gl, vertexShaderSource, fragmentShaderSource)
            program.use(gl)

            val position = program.attributes.getValue("aVertexPosition")

            return FirstExample(buffer, indexBuffer, position.location, indices.size)
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
                fragColor = vec4(0.0, 1.0, 0.0, 1.0);
            }
            """.trimIndent()
    }
}
