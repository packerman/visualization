package framework.geometry

import framework.core.Attribute
import framework.core.Index
import framework.core.Program
import framework.core.Supplier
import framework.math.Vector3
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.LINES
import web.gl.WebGL2RenderingContext.Companion.LINE_LOOP
import web.gl.WebGL2RenderingContext.Companion.LINE_STRIP
import web.gl.WebGL2RenderingContext.Companion.POINTS
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.gl.WebGL2RenderingContext.Companion.TRIANGLE_FAN
import web.gl.WebGL2RenderingContext.Companion.TRIANGLE_STRIP

enum class Mode(val value: GLenum) {
    Points(POINTS),
    Lines(LINES),
    LineStrip(LINE_STRIP),
    LineLoop(LINE_LOOP),
    Triangles(TRIANGLES),
    TriangleStrip(TRIANGLE_STRIP),
    TriangleFan(TRIANGLE_FAN),
}

class Geometry(
    private val attributes: Map<String, Attribute>,
    val vertexCount: Int,
    private val index: Index?,
) {

    fun buildVertexArray(gl: WebGL2RenderingContext, program: Program) {
        for ((name, attribute) in attributes) {
            attribute.uploadData(gl)
            program.getAttribute(name)?.let { active ->
                attribute.associateLocation(gl, active.location)
            }
        }
        index?.bind(gl)
    }

    fun draw(gl: WebGL2RenderingContext, mode: Mode) {
        if (index != null) {
            gl.drawElements(mode.value, index.count, index.type, 0)
        } else {
            gl.drawArrays(mode.value, 0, vertexCount)
        }
    }

    val hasIndex: Boolean
        get() = index != null

    companion object {
        const val POSITION = "a_position"
        const val COLOR_0 = "a_color_0"
        const val TEXCOORD_0 = "a_texcoord_0"
        const val NORMAL = "a_normal"

        operator fun invoke(attributes: Map<String, Attribute>, index: Index?): Geometry {
            val vertexCount = attributes.asSequence()
                .map { (_, attribute) -> attribute.count }
                .distinct()
                .single()
            return Geometry(attributes, vertexCount, index)
        }
    }
}

class GeometryBuilder(private val gl: WebGL2RenderingContext) {

    private val attributes = mutableMapOf<String, Attribute>()

    private var index: Index? = null

    fun attribute(name: String, array: Array<Array<Float>>) {
        attributes[name] = Attribute(gl, array)
    }

    fun attribute(name: String, list: List<Array<Float>>) {
        attributes[name] = Attribute(gl, list)
    }

    fun attribute(name: String, array: Array<Float>, size: Int) {
        attributes[name] = Attribute(gl, array, size)
    }

    fun attribute(name: String, array: Array<Vector3>) {
        attributes[name] = Attribute(gl, array)
    }

    fun index(array: Array<Short>) {
        require(index == null)
        index = Index(gl, array)
    }

    fun build(): Geometry = Geometry(attributes, index)
}

fun geometry(gl: WebGL2RenderingContext, block: (GeometryBuilder).() -> Unit): Geometry =
    GeometryBuilder(gl).apply(block).build()

fun geometry(block: (GeometryBuilder).() -> Unit): Supplier<Geometry> = { gl ->
    GeometryBuilder(gl).apply(block).build()
}
