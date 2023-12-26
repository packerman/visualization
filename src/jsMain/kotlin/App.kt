import common.run
import examples.WallExample
import web.dom.document
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.RENDERER
import web.gl.WebGL2RenderingContext.Companion.SHADING_LANGUAGE_VERSION
import web.gl.WebGL2RenderingContext.Companion.VENDOR
import web.gl.WebGL2RenderingContext.Companion.VERSION
import web.html.HTMLCanvasElement

fun main() {
    val canvas = document.getElementById("render") as HTMLCanvasElement
    val gl = requireNotNull(canvas.getContext(WebGL2RenderingContext.ID)) {
        "Cannot initialize WebGL2 context"
    }

    gl.clearColor(0.5, 0.5, 0.5, 1)
    gl.enable(WebGL2RenderingContext.DEPTH_TEST)

    val app = WallExample.create(gl)

    logWebGLParameters(gl)

    run(gl, app)
}

private fun logWebGLParameters(gl: WebGL2RenderingContext) {
    console.log(gl.getParameter(VERSION))
    console.log(gl.getParameter(SHADING_LANGUAGE_VERSION))
    console.log(gl.getParameter(VENDOR))
    console.log(gl.getParameter(RENDERER))
}
