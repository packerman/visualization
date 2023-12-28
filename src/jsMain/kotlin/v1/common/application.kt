package v1.common

import framework.core.KeyState
import web.gl.WebGL2RenderingContext

@Deprecated(message = "")
interface Application {
    fun update(elapsed: Double, keyState: KeyState) {}

    fun render(gl: WebGL2RenderingContext)
}
