package examples.transformations

import framework.core.*
import framework.core.Attribute.Companion.attribute
import framework.core.Uniform.Companion.uniform
import framework.math.Matrix4
import framework.math.toRadians
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.COLOR_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_TEST
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGLVertexArrayObject

@Suppress("unused")
class MoveTriangle private constructor(
    private val program: Program,
    private val shapes: List<Shape>,
    private val modelMatrix: Uniform<Matrix4>
) : Application {

    private val moveSpeed = 0.5f
    private val turnSpeed = toRadians(90f)

    override fun update(elapsed: Double, keyState: KeyState) {
        val moveAmount = moveSpeed * elapsed.toFloat() / 1000f
        val turnAmount = turnSpeed * elapsed.toFloat() / 1000f

        if (keyState.isPressed("KeyW")) {
            val m = Matrix4.translation(0f, moveAmount, 0f)
            modelMatrix.data = m * modelMatrix.data
        }
        if (keyState.isPressed("KeyS")) {
            val m = Matrix4.translation(0f, -moveAmount, 0f)
            modelMatrix.data = m * modelMatrix.data
        }
        if (keyState.isPressed("KeyA")) {
            val m = Matrix4.translation(-moveAmount, 0f, 0f)
            modelMatrix.data = m * modelMatrix.data
        }
        if (keyState.isPressed("KeyD")) {
            val m = Matrix4.translation(moveAmount, 0f, 0f)
            modelMatrix.data = m * modelMatrix.data
        }
        if (keyState.isPressed("KeyZ")) {
            val m = Matrix4.translation(0f, 0f, moveAmount)
            modelMatrix.data = m * modelMatrix.data
        }
        if (keyState.isPressed("KeyX")) {
            val m = Matrix4.translation(0f, 0f, -moveAmount)
            modelMatrix.data = m * modelMatrix.data
        }
    }

    override fun render(gl: WebGL2RenderingContext) {
        gl.clear(COLOR_BUFFER_BIT.toInt() or DEPTH_BUFFER_BIT.toInt())
        for (shape in shapes) {
            shape.render(gl)
        }
    }

    companion object : Initializer<MoveTriangle> {
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

        override fun initialize(gl: WebGL2RenderingContext): MoveTriangle {
            gl.clearColor(0.1, 0.1, 0.1, 1.0)
            gl.enable(DEPTH_TEST)
            val program = Program.build(
                gl,
                """
                    in vec3 a_position;
                    
                    uniform mat4 u_ProjectionMatrix;
                    uniform mat4 u_ModelMatrix;
                    
                    void main() {
                        gl_Position = u_ProjectionMatrix * u_ModelMatrix * vec4(a_position, 1.0);
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
            val vertexArray = setupVertexArray(
                gl, program, mapOf(
                    "a_position" to attribute(
                        arrayOf(
                            0f, 0.2f, 0f,
                            0.1f, -0.2f, 0f,
                            -0.1f, -0.2f, 0f
                        ), 3
                    )
                )
            )
            val modelMatrix = uniform(Matrix4.translation(0f, 0f, -1f))
            val shapes = listOf(
                Shape(
                    program, vertexArray, mapOf(
                        "u_ProjectionMatrix" to uniform(Matrix4.perspective()),
                        "u_ModelMatrix" to modelMatrix
                    ), TRIANGLES
                )
            )
            return MoveTriangle(program, shapes, modelMatrix)
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
