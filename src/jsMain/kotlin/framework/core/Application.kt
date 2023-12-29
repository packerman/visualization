package framework.core

import web.animations.FrameRequestId
import web.animations.requestAnimationFrame
import web.dom.document
import web.gl.WebGL2RenderingContext
import web.html.HTMLCanvasElement
import kotlin.js.Date

interface Application {

    fun start(gl: WebGL2RenderingContext) {}

    fun update(elapsed: Double, keyState: KeyState) {}

    fun render(gl: WebGL2RenderingContext)

    companion object {
        fun <A : Application> run(canvas: HTMLCanvasElement, initializer: Initializer<A>) {
            val gl = requireNotNull(canvas.getContext(WebGL2RenderingContext.ID)) {
                "Cannot initialize WebGL2 context"
            }
            val application = initializer.initialize(gl)
            var lastTime = Date.now()

            val input = Input()
            input.attachTo(document)

            application.start(gl)

            requestAnimationLoop { currentTime ->
                resizeCanvasToDisplaySize(gl)
                gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)
                application.update(currentTime - lastTime, input)
                input.reset()
                application.render(gl)
                lastTime = currentTime
            }
        }

        private fun requestAnimationLoop(frame: (Double) -> Unit): FrameRequestId {
            fun loop(time: Double): FrameRequestId {
                val id = requestAnimationFrame(::loop)
                frame(time)
                return id
            }
            return loop(Date.now())
        }

        private fun resizeCanvasToDisplaySize(gl: WebGL2RenderingContext) {
            val canvas = gl.canvas as HTMLCanvasElement
            val width = canvas.clientWidth
            val height = canvas.clientHeight
            if (canvas.width != width || canvas.height != height) {
                canvas.width = width
                canvas.height = height
            }
        }
    }
}
