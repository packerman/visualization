package examples.intro

import framework.core.Base
import framework.core.Initializer
import web.gl.WebGL2RenderingContext

@Suppress("unused")
class Test : Base {

    override fun render(gl: WebGL2RenderingContext) {
    }

    companion object : Initializer<Test> {
        override fun initialize(gl: WebGL2RenderingContext): Test {
            console.log(gl.getParameter(WebGL2RenderingContext.VERSION))
            console.log(gl.getParameter(WebGL2RenderingContext.SHADING_LANGUAGE_VERSION))
            console.log(gl.getParameter(WebGL2RenderingContext.VENDOR))
            console.log(gl.getParameter(WebGL2RenderingContext.RENDERER))
            return Test()
        }
    }
}
