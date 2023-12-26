package common

import web.gl.WebGL2RenderingContext

typealias Supplier<T> = (gl: WebGL2RenderingContext) -> T

interface Creator<T> {
    fun create(gl: WebGL2RenderingContext): T
}
