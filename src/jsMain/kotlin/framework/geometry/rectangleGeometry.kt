package framework.geometry

import framework.geometry.Geometry.Companion.COLOR
import framework.geometry.Geometry.Companion.POSITION
import web.gl.WebGL2RenderingContext

fun rectangleGeometry(gl: WebGL2RenderingContext, width: Float = 1f, height: Float = 1f): Geometry = geometry(gl) {
    attribute(
        POSITION,
        arrayOf(
            arrayOf(-width / 2, -height / 2, 0f),
            arrayOf(width / 2, -height / 2, 0f),
            arrayOf(-width / 2, height / 2, 0f),
            arrayOf(width / 2, height / 2, 0f)
        )
    )
    attribute(
        COLOR,
        arrayOf(arrayOf(1f, 1f, 1f), arrayOf(1f, 0f, 0f), arrayOf(0f, 1f, 0f), arrayOf(0f, 0f, 1f))
    )
    index(arrayOf(0, 1, 3, 0, 3, 2))
}
