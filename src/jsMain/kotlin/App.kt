import js.typedarrays.Float32Array
import js.typedarrays.Uint16Array
import web.dom.document
import web.gl.*
import web.gl.WebGL2RenderingContext.Companion.ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.COLOR_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.COMPILE_STATUS
import web.gl.WebGL2RenderingContext.Companion.DEPTH_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.FRAGMENT_SHADER
import web.gl.WebGL2RenderingContext.Companion.LINK_STATUS
import web.gl.WebGL2RenderingContext.Companion.STATIC_DRAW
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGL2RenderingContext.Companion.VERTEX_SHADER
import web.html.HTMLCanvasElement

class Application(
    private val buffer: WebGLBuffer,
    private val indexBuffer: WebGLBuffer,
    private val positionLocation: GLint,
    private val count: Int
) {
    fun draw(gl: WebGL2RenderingContext) {
        gl.clear(COLOR_BUFFER_BIT.toInt() or DEPTH_BUFFER_BIT.toInt())
        gl.viewport(0, 0, (gl.canvas as HTMLCanvasElement).width, (gl.canvas as HTMLCanvasElement).height)

        gl.bindBuffer(ARRAY_BUFFER, buffer)
        gl.vertexAttribPointer(positionLocation, 3, WebGL2RenderingContext.FLOAT, 0, 0, 0)
        gl.enableVertexAttribArray(positionLocation)

        gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indexBuffer)

        gl.drawElements(TRIANGLES, count, WebGL2RenderingContext.UNSIGNED_SHORT, 0)
    }

    companion object {
        fun initialize(gl: WebGL2RenderingContext): Application {
            val vertices = arrayOf(
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f
            )
            val indices: Array<Short> = arrayOf(0, 1, 2, 0, 2, 3)

            val buffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
            gl.bindBuffer(ARRAY_BUFFER, buffer)
            gl.bufferData(ARRAY_BUFFER, Float32Array(vertices), STATIC_DRAW)

            val indexBuffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
            gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indexBuffer)
            gl.bufferData(ELEMENT_ARRAY_BUFFER, Uint16Array(indices), STATIC_DRAW)

            gl.bindBuffer(ARRAY_BUFFER, null)
            gl.bindBuffer(ELEMENT_ARRAY_BUFFER, null)

            val program = buildProgram(gl, vertexShaderSource, fragmentShaderSource)
            gl.useProgram(program)

            val positionLocation = gl.getAttribLocation(program, "aVertexPosition")
            check(positionLocation.toInt() >= 0) { "Unknown position location" }

            return Application(buffer, indexBuffer, positionLocation, indices.size)
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
                fragColor = vec4(1.0, 0.0, 0.0, 1.0);
            }
            """.trimIndent()
    }
}

fun main() {
    val canvas = document.getElementById("render") as HTMLCanvasElement
    val gl = requireNotNull(canvas.getContext(WebGL2RenderingContext.ID)) {
        "Cannot initialize WebGL2 context"
    }

    gl.clearColor(0.5, 0.5, 0.5, 1)
    gl.clear(COLOR_BUFFER_BIT)

    val app = Application.initialize(gl)

    app.draw(gl)
}

fun buildProgram(gl: WebGL2RenderingContext, vertexSource: String, fragmentSource: String): WebGLProgram {
    val vertexShader = compileShader(gl, vertexSource, VERTEX_SHADER)
    val fragmentShader = compileShader(gl, fragmentSource, FRAGMENT_SHADER)
    return linkProgram(gl, vertexShader, fragmentShader)
}

fun compileShader(gl: WebGL2RenderingContext, source: String, type: GLenum): WebGLShader {
    val shader = checkNotNull(gl.createShader(type)) { "Cannot create shader." }
    gl.shaderSource(shader, source)
    gl.compileShader(shader)
    if (!(gl.getShaderParameter(shader, COMPILE_STATUS) as Boolean)) {
        console.error(gl.getShaderInfoLog(shader))
    }
    return shader
}

fun linkProgram(gl: WebGL2RenderingContext, vertex: WebGLShader, fragment: WebGLShader): WebGLProgram {
    val program = checkNotNull(gl.createProgram()) { "Cannot create program." }
    gl.attachShader(program, vertex)
    gl.attachShader(program, fragment)
    gl.linkProgram(program)
    if (!(gl.getProgramParameter(program, LINK_STATUS) as Boolean)) {
        console.error(gl.getProgramInfoLog(program))
    }
    return program
}