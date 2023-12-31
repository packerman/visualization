package examples.transformations

import framework.core.*
import framework.core.Uniform.Companion.uniform
import framework.geometry.Geometry
import framework.geometry.Geometry.Companion.POSITION
import framework.geometry.geometry
import framework.math.Matrix4
import framework.math.toRadians
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.COLOR_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_TEST
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGLUniformLocation
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

        if (keyState.isPressed("w")) {
            modelMatrix.data = Matrix4.translation(0f, moveAmount, 0f) * modelMatrix.data
        }
        if (keyState.isPressed("s")) {
            modelMatrix.data = Matrix4.translation(0f, -moveAmount, 0f) * modelMatrix.data
        }
        if (keyState.isPressed("a")) {
            modelMatrix.data = Matrix4.translation(-moveAmount, 0f, 0f) * modelMatrix.data
        }
        if (keyState.isPressed("d")) {
            modelMatrix.data = Matrix4.translation(moveAmount, 0f, 0f) * modelMatrix.data
        }
        if (keyState.isPressed("z")) {
            modelMatrix.data = Matrix4.translation(0f, 0f, moveAmount) * modelMatrix.data
        }
        if (keyState.isPressed("x")) {
            modelMatrix.data = Matrix4.translation(0f, 0f, -moveAmount) * modelMatrix.data
        }

        if (keyState.isPressed("q")) {
            modelMatrix.data = Matrix4.rotationZ(turnAmount) * modelMatrix.data
        }
        if (keyState.isPressed("e")) {
            modelMatrix.data = Matrix4.rotationZ(-turnAmount) * modelMatrix.data
        }

        if (keyState.isPressed("i")) {
            modelMatrix.data.timesAssign(Matrix4.translation(0f, moveAmount, 0f))
        }
        if (keyState.isPressed("j")) {
            modelMatrix.data.timesAssign(Matrix4.translation(0f, -moveAmount, 0f))
        }
        if (keyState.isPressed("k")) {
            modelMatrix.data.timesAssign(Matrix4.translation(-moveAmount, 0f, 0f))
        }
        if (keyState.isPressed("l")) {
            modelMatrix.data.timesAssign(Matrix4.translation(moveAmount, 0f, 0f))
        }

        if (keyState.isPressed("u")) {
            modelMatrix.data.timesAssign(Matrix4.rotationZ(turnAmount))
        }
        if (keyState.isPressed("o")) {
            modelMatrix.data.timesAssign(Matrix4.rotationZ(-turnAmount))
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
            val id: WebGLVertexArrayObject,
            val geometry: Geometry
        )

        private data class Shape(
            val program: Program,
            val vertexArray: VertexArray,
            val uniforms: List<Pair<Uniform<*>, WebGLUniformLocation?>>,
            val mode: GLenum
        ) {
            fun render(gl: WebGL2RenderingContext) {
                program.use(gl)
                uploadData(gl, uniforms)
                gl.bindVertexArray(vertexArray.id)
                gl.drawArrays(mode, 0, vertexArray.geometry.vertexCount)
            }

            companion object {
                fun create(
                    program: Program,
                    vertexArray: VertexArray,
                    uniforms: Map<String, Uniform<*>>,
                    mode: GLenum
                ): Shape {
                    return Shape(
                        program,
                        vertexArray,
                        associateUniforms(program, uniforms),
                        mode
                    )
                }

                private fun uploadData(
                    gl: WebGL2RenderingContext,
                    uniformLocations: List<Pair<Uniform<*>, WebGLUniformLocation?>>
                ) {
                    for ((uniform, location) in uniformLocations) {
                        uniform.uploadData(gl, location)
                    }
                }

                private fun associateUniforms(
                    program: Program,
                    uniforms: Map<String, Uniform<*>>
                ): List<Pair<Uniform<*>, WebGLUniformLocation?>> =
                    uniforms.map { (name, uniform) -> uniform to program.getUniform(name).location }
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
                gl, program, geometry(gl) {
                    attribute(
                        POSITION,
                        arrayOf(
                            0f, 0.2f, 0f,
                            0.1f, -0.2f, 0f,
                            -0.1f, -0.2f, 0f
                        ), 3
                    )
                }
            )

            val modelMatrix = uniform(Matrix4.translation(0f, 0f, -1f))
            val shapes = listOf(
                Shape.create(
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
            geometry: Geometry
        ): VertexArray {
            val vertexArray = requireNotNull(gl.createVertexArray()) {
                "Cannot create vertex array object"
            }
            geometry.buildArray(gl, program)
            return VertexArray(vertexArray, geometry)
        }
    }
}
