package examples.intro

import framework.core.*
import framework.core.Attribute.Companion.attribute
import framework.core.Uniform.Companion.uniform
import framework.math.Vector3
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGLVertexArrayObject

@Suppress("unused")
class MoveWithKeyboard private constructor(
    private val program: Program,
    private val shapes: List<Shape>,
    private val translation: Uniform<Vector3>
) : Application {

    private val speed = 0.5f

    override fun update(elapsed: Double, keyState: KeyState) {
        val distance = speed * elapsed.toFloat() / 1000f
        if (keyState.isPressed("ArrowLeft")) {
            translation.data.x -= distance
        }
        if (keyState.isPressed("ArrowRight")) {
            translation.data.x += distance
        }
        if (keyState.isPressed("ArrowDown")) {
            translation.data.y -= distance
        }
        if (keyState.isPressed("ArrowUp")) {
            translation.data.y += distance
        }
    }

    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT)
        for (shape in shapes) {
            shape.render(gl)
        }
    }

    companion object : Initializer<MoveWithKeyboard> {
        private data class VertexArray(
            val glObject: WebGLVertexArrayObject,
            val count: Int
        )

        private data class Shape(
            val program: Program,
            val vertexArray: VertexArray,
            val uniforms: Map<String, Uniform<*>>,
            val mode: GLenum
        ) {
            fun render(gl: WebGL2RenderingContext) {
                program.use(gl)
                uploadData(gl, program, uniforms)
                gl.bindVertexArray(vertexArray.glObject)
                gl.drawArrays(mode, 0, vertexArray.count)
            }
        }

        override fun initialize(gl: WebGL2RenderingContext): MoveWithKeyboard {
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
                gl, program, mapOf(
                    "a_position" to attribute(
                        arrayOf(
                            0f, 0.2f, 0f, 0.2f, -0.2f, 0f, -0.2f, -0.2f, 0f
                        ), 3
                    )
                )
            )
            val translation = uniform(Vector3(-0.5f, 0f, 0f))
            val shapes = listOf(
                Shape(
                    program, vertexArray, mapOf(
                        "u_Translation" to translation,
                        "u_BaseColor" to uniform(Vector3(1f, 0f, 0f))
                    ), TRIANGLES
                )
            )
            return MoveWithKeyboard(program, shapes, translation)
        }

        private fun setupVertexArray(
            gl: WebGL2RenderingContext,
            program: Program,
            attributes: Map<String, AttributeInitializer>
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
                attributes.asSequence()
                    .map { (_, attribute) -> attribute.count }
                    .distinct()
                    .single()

            )
        }

        private fun uploadData(gl: WebGL2RenderingContext, program: Program, uniforms: Map<String, Uniform<*>>) {
            for ((name, uniform) in uniforms) {
                uniform.uploadData(gl, program.getUniform(name).location)
            }
        }
    }
}
