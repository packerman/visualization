package examples.intro

import framework.core.Application
import framework.core.Initializer
import framework.core.Program
import framework.geometry.Geometry
import framework.geometry.geometry
import framework.math.Vector3
import js.core.toTypedArray
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.LINE_LOOP
import web.gl.WebGL2RenderingContext.Companion.POINTS
import web.gl.WebGL2RenderingContext.Companion.TRIANGLE_FAN
import web.gl.WebGLVertexArrayObject

@Suppress("unused")
class ColorBuffer(
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

    companion object : Initializer<ColorBuffer> {
        data class VertexArray(
            val id: WebGLVertexArrayObject,
            val geometry: Geometry
        )

        data class Shape(
            val vertexArray: VertexArray,
            val mode: GLenum
        ) {
            fun render(gl: WebGL2RenderingContext) {
                gl.bindVertexArray(vertexArray.id)
                gl.drawArrays(mode, 0, vertexArray.geometry.vertexCount)
            }
        }

        override fun initialize(gl: WebGL2RenderingContext): ColorBuffer {
            gl.clearColor(0.0, 0.0, 0.0, 1.0)
            gl.lineWidth(5f)
            val program = Program.build(
                gl,
                """
                    in vec3 a_position;
                    in vec3 a_color_0;
                    
                    out vec3 v_Color;
                    
                    void main() {
                        gl_PointSize = 5.0;
                        gl_Position = vec4(a_position, 1.0);
                        v_Color = a_color_0;
                    }
                """,
                """
                    in vec3 v_Color;
                    out vec4 fragColor;
                    
                    void main() {
                        fragColor = vec4(v_Color, 1.0);
                    }
                """
            )
            program.use(gl)
            val shapes = listOf(
                createShape(0.5f, Vector3(0f, 0.5f, 0f), POINTS),
                createShape(0.5f, Vector3(-0.5f, -0.5f, 0f), LINE_LOOP),
                createShape(0.5f, Vector3(0.5f, -0.5f, 0f), TRIANGLE_FAN)
            ).map { it(gl, program) }
            return ColorBuffer(program, shapes)
        }

        private fun createShape(
            scale: Float,
            position: Vector3,
            mode: GLenum
        ): (WebGL2RenderingContext, Program) -> Shape {
            return { gl, program ->
                Shape(
                    setupVertexArray(
                        gl, program,
                        geometry(gl) {
                            attribute("a_position",
                                sequenceOf(
                                    Vector3(0.8f, 0f, 0f),
                                    Vector3(0.4f, 0.6f, 0f),
                                    Vector3(-0.4f, 0.6f, 0f),
                                    Vector3(-0.8f, 0f, 0f),
                                    Vector3(-0.4f, -0.6f, 0f),
                                    Vector3(0.4f, -0.6f, 0f)
                                ).map { it * scale }
                                    .map { it + position }
                                    .toTypedArray()
                            )
                            attribute(
                                "a_color_0",
                                arrayOf(
                                    1f, 0f, 0f,
                                    1f, 0.5f, 0f,
                                    1f, 1f, 0f,
                                    0f, 1f, 0f,
                                    0f, 0f, 1f,
                                    0.5f, 0f, 1f
                                ), 3
                            )
                        }
                    ),
                    mode
                )
            }
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
    }
}
