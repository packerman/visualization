package common

import web.gl.WebGL2RenderingContext
import kotlin.js.Date

interface Application {
    fun update(elapsed: Double) {}

    fun render(gl: WebGL2RenderingContext)
}

interface Initializer<A: Application> {
    fun initialize(gl: WebGL2RenderingContext): A
}

fun run(gl: WebGL2RenderingContext, application: Application) {
    var lastTime = Date.now()
    requestAnimationLoop { currentTime ->
        resizeCanvasToDisplaySize(gl)
        application.update(currentTime - lastTime)
        application.render(gl)
        lastTime = currentTime
    }
}