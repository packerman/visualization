package examples

import common.Application
import common.Creator
import web.gl.WebGL2RenderingContext

class ModelViewExample: Creator<Application> {
    override fun create(gl: WebGL2RenderingContext): Application {
        return object : Application {
            override fun render(gl: WebGL2RenderingContext) {
                TODO("Not yet implemented")
            }
        }
    }
}
