import common.run
import examples.MovingLightExample
import web.dom.document
import web.gl.WebGL2RenderingContext
import web.html.HTMLCanvasElement

fun main() {
    val canvas = document.getElementById("render") as HTMLCanvasElement
    val gl = requireNotNull(canvas.getContext(WebGL2RenderingContext.ID)) {
        "Cannot initialize WebGL2 context"
    }

    gl.clearColor(0.5, 0.5, 0.5, 1)
    gl.enable(WebGL2RenderingContext.DEPTH_TEST)

    val app = MovingLightExample.initialize(gl)

    run(gl, app)
}
