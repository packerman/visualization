package framework.core

import framework.math.Vector3
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.BLEND
import web.gl.WebGL2RenderingContext.Companion.COLOR_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_TEST
import web.gl.WebGL2RenderingContext.Companion.ONE_MINUS_SRC_ALPHA
import web.gl.WebGL2RenderingContext.Companion.SRC_ALPHA

class Renderer private constructor() {

    fun render(gl: WebGL2RenderingContext, scene: Node, camera: Camera) {
        gl.clear(COLOR_BUFFER_BIT.toInt() or DEPTH_BUFFER_BIT.toInt())
        camera.aspectRatio = gl.drawingBufferWidth.toFloat() / gl.drawingBufferHeight.toFloat()
        camera.updateViewMatrix()

        val descendants = scene.descendants

        descendants.filterIsInstance<Mesh>().forEach { mesh ->
            mesh.render(gl, camera)
        }
    }

    companion object {
        operator fun invoke(
            gl: WebGL2RenderingContext,
            clearColor: Vector3 = Vector3(0f, 0f, 0f)
        ): Renderer {
            gl.enable(DEPTH_TEST)
            gl.clearColor(clearColor.r, clearColor.g, clearColor.b, 1f)
            gl.enable(BLEND)
            gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA)
            return Renderer()
        }
    }
}
