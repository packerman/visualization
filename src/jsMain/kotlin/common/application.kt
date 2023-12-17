package common

import web.dom.document
import web.gl.WebGL2RenderingContext
import kotlin.js.Date

interface Application {
    fun update(elapsed: Double, keyState: KeyState) {}

    fun render(gl: WebGL2RenderingContext)
}

interface Initializer<A: Application> {
    fun initialize(gl: WebGL2RenderingContext): A
}

fun run(gl: WebGL2RenderingContext, application: Application) {
    var lastTime = Date.now()

    val keyState = KeyState()
    document.onkeydown = keyState::setPressed
    document.onkeyup = keyState::setReleased

    requestAnimationLoop { currentTime ->
        resizeCanvasToDisplaySize(gl)
        application.update(currentTime - lastTime, keyState)
        application.render(gl)
        lastTime = currentTime
    }
}