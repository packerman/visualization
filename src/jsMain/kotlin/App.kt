import examples.intro.AnimationIntro
import framework.core.Base.Companion.run
import web.dom.document
import web.html.HTMLCanvasElement

fun main() {
    val canvas = document.getElementById("render") as HTMLCanvasElement

    run(canvas, AnimationIntro)
}

