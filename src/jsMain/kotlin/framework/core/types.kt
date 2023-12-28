package framework.core

import web.gl.WebGL2RenderingContext

interface Initializer<T> {
    fun initialize(gl: WebGL2RenderingContext): T
}

typealias Supplier<T> = (gl: WebGL2RenderingContext) -> T
