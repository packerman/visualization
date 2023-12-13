import examples.SimplePipelineExample
import examples.VaoExample
import web.dom.document
import web.gl.*
import web.gl.WebGL2RenderingContext.Companion.COLOR_BUFFER_BIT
import web.html.HTMLCanvasElement

fun main() {
    val canvas = document.getElementById("render") as HTMLCanvasElement
    val gl = requireNotNull(canvas.getContext(WebGL2RenderingContext.ID)) {
        "Cannot initialize WebGL2 context"
    }

    gl.clearColor(0.5, 0.5, 0.5, 1)
    gl.clear(COLOR_BUFFER_BIT)

    val app = SimplePipelineExample.initialize(gl)

    app.draw(gl)
}
