package framework.core

import framework.geometry.Geometry
import framework.geometry.Mode
import framework.material.Material
import framework.material.MaterialImpl.Companion.MODEL_MATRIX
import framework.material.MaterialImpl.Companion.PROJECTION_MATRIX
import framework.material.MaterialImpl.Companion.VIEW_MATRIX
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.ARRAY_BUFFER
import web.gl.WebGL2RenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import web.gl.WebGLVertexArrayObject

class Mesh private constructor(
    private val geometry: Geometry,
    private val material: Material,
    private val vertexArray: WebGLVertexArrayObject,
    private val mode: Mode
) : Node by NodeImpl() {

    var visible: Boolean = true

    fun render(gl: WebGL2RenderingContext, camera: Camera) {
        if (!visible) return
        with(material) {
            program.use(gl)
            setUniform(MODEL_MATRIX, worldMatrix)
            setUniform(VIEW_MATRIX, camera.viewMatrix)
            setUniform(PROJECTION_MATRIX, camera.projectionMatrix)
            uploadData(gl)
            updateRenderSettings(gl)
        }
        gl.bindVertexArray(vertexArray)
        geometry.draw(gl, mode)
    }

    companion object {
        operator fun invoke(
            gl: WebGL2RenderingContext,
            geometry: Geometry,
            material: Material,
            mode: Mode = Mode.Triangles
        ): Mesh {
            val array = requireNotNull(gl.createVertexArray())
            gl.bindVertexArray(array)
            geometry.buildArray(gl, material.program)
            gl.bindVertexArray(null)
            gl.bindBuffer(ARRAY_BUFFER, null)
            if (geometry.hasIndex) gl.bindBuffer(ELEMENT_ARRAY_BUFFER, null)
            return Mesh(geometry, material, array, mode)
        }
    }
}

fun mesh(
    gl: WebGL2RenderingContext,
    geometry: Supplier<Geometry>,
    material: Supplier<Material>,
    mode: Mode = Mode.Triangles
): Mesh = Mesh(gl, geometry(gl), material(gl), mode)
