package framework.geometry

import framework.core.Supplier
import framework.geometry.Geometry.Companion.COLOR_0
import framework.geometry.Geometry.Companion.POSITION
import framework.geometry.Geometry.Companion.TEXCOORD_0

fun rectangleGeometry(width: Float = 1f, height: Float = 1f): Supplier<Geometry> = { gl ->
    geometry(gl) {
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
            COLOR_0,
            arrayOf(arrayOf(1f, 1f, 1f), arrayOf(1f, 0f, 0f), arrayOf(0f, 1f, 0f), arrayOf(0f, 0f, 1f))
        )
        attribute(
            TEXCOORD_0,
            arrayOf(arrayOf(0f, 0f), arrayOf(1f, 0f), arrayOf(0f, 1f), arrayOf(1f, 1f))
        )
        index(arrayOf(0, 1, 3, 0, 3, 2))
    }
}
