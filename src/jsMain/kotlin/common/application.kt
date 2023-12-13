package common

import web.gl.WebGL2RenderingContext

interface Application {
    fun draw(gl: WebGL2RenderingContext)
}

interface Initializer<A: Application> {
    fun initialize(gl: WebGL2RenderingContext): A
}