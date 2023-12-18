package common

import js.typedarrays.Float32Array
import js.typedarrays.Uint16Array
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.FLOAT
import web.gl.WebGL2RenderingContext.Companion.FLOAT_VEC2
import web.gl.WebGL2RenderingContext.Companion.FLOAT_VEC3
import web.gl.WebGL2RenderingContext.Companion.FLOAT_VEC4
import web.gl.WebGLBuffer
import web.gl.WebGLVertexArrayObject

typealias AttributeSupplier = (WebGL2RenderingContext) -> Attribute

class Attribute(private val arrayBuffer: WebGLBuffer, private val length: Int) {

    fun getCount(size: Int): Int {
        require(length % size == 0)
        return length / size
    }

    companion object {
        fun attribute(floatArray: Array<Float>): AttributeSupplier {
            return { gl ->
                val buffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
                gl.bindBuffer(ARRAY_BUFFER, buffer)
                gl.bufferData(ARRAY_BUFFER, Float32Array(floatArray), WebGL2RenderingContext.STATIC_DRAW)
                Attribute(buffer, floatArray.size)
            }
        }
    }
}

class Pipeline(
    val attributes: Map<String, AttributeSupplier>,
    val uniforms: UniformMap,
    val indices: Array<Short>,
    val mode: GLenum,
    val vertexShaderSource: String,
    val fragmentShaderSource: String
) {

    fun initialize(gl: WebGL2RenderingContext): Renderable {
        val vao = requireNotNull(gl.createVertexArray()) { "Cannot create VAO" }

        val program = Program.build(gl, vertexShaderSource, fragmentShaderSource)
        program.use(gl)

        gl.bindVertexArray(vao)

        for ((name, supplyAttribute) in attributes) {
            val active = program.attributes.getValue(name)

            supplyAttribute(gl)

            val size = typeToSize.getValue(active.type)
            val type = typeToComponentType.getValue(active.type)

            gl.vertexAttribPointer(active.location, size, type, 0, 0, 0)
            gl.enableVertexAttribArray(active.location)
        }

        val indexBuffer = requireNotNull(gl.createBuffer()) { "Cannot create buffer" }
        gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indexBuffer)
        gl.bufferData(ELEMENT_ARRAY_BUFFER, Uint16Array(indices), WebGL2RenderingContext.STATIC_DRAW)

        val activeUniforms = uniforms.asSequence()
            .map { (name, uniform) -> uniform to program.uniforms.getValue(name) }
            .toList()

        gl.bindVertexArray(null)
        gl.bindBuffer(ARRAY_BUFFER, null)
        gl.bindBuffer(ELEMENT_ARRAY_BUFFER, null)

        return Renderable(program, vao, indices.size, mode, activeUniforms)
    }

    companion object {
        private val typeToSize = mapOf(
            FLOAT_VEC2 to 2,
            FLOAT_VEC3 to 3,
            FLOAT_VEC4 to 4,
        )

        private val typeToComponentType = mapOf(
            FLOAT_VEC2 to FLOAT,
            FLOAT_VEC3 to FLOAT,
            FLOAT_VEC4 to FLOAT,
        )
    }
}

class Renderable(
    val program: Program,
    val vertexArray: WebGLVertexArrayObject,
    val count: Int,
    val mode: GLenum,
    val uniforms: List<Pair<Uniform<*>, ActiveUniform>>
) {
    fun render(gl: WebGL2RenderingContext) {
        program.use(gl)

        for ((uniform, active) in uniforms) {
            uniform.update(gl, active.location)
        }

        gl.bindVertexArray(vertexArray)
        gl.drawElements(mode, count, WebGL2RenderingContext.UNSIGNED_SHORT, 0)
        gl.bindVertexArray(null)
    }
}