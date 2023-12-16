package common

import web.animations.FrameRequestId
import web.animations.requestAnimationFrame
import web.gl.WebGL2RenderingContext
import web.html.HTMLCanvasElement
import kotlin.js.Date

fun requestAnimationLoop(frame: (Double) -> Unit): FrameRequestId {
    fun loop(time: Double): FrameRequestId {
        val id = requestAnimationFrame(::loop)
        frame(time)
        return id
    }
    return loop(Date.now())
}

fun resizeCanvasToDisplaySize(gl: WebGL2RenderingContext) {
    val canvas = gl.canvas as HTMLCanvasElement
    val width = canvas.clientWidth
    val height = canvas.clientHeight
    if (canvas.width != width || canvas.height != height) {
        canvas.width = width
        canvas.height = height
    }
}