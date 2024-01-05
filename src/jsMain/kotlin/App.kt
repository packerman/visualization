import examples.texture.TexturedRectangle
import framework.core.Application.Companion.run
import web.dom.document
import web.html.HTMLCanvasElement

fun main() {
    val canvas = document.getElementById("render") as HTMLCanvasElement

    run(canvas, TexturedRectangle)
}

