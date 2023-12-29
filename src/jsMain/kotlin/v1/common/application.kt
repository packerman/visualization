package v1.common

import framework.core.Input
import web.gl.WebGL2RenderingContext

@Deprecated(message = "")
interface Application {
    fun update(elapsed: Double, input: Input) {}

    fun render(gl: WebGL2RenderingContext)
}
