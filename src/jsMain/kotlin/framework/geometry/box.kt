package framework.geometry

import framework.core.Supplier
import framework.geometry.Geometry.Companion.COLOR_0
import framework.geometry.Geometry.Companion.POSITION
import framework.geometry.Geometry.Companion.TEXCOORD_0

fun boxGeometry(width: Float = 1f, height: Float = 1f, depth: Float = 1f): Supplier<Geometry> = { gl ->
    val p = arrayOf(
        arrayOf(-width / 2, -height / 2, -depth / 2),
        arrayOf(width / 2, -height / 2, -depth / 2),
        arrayOf(-width / 2, height / 2, -depth / 2),
        arrayOf(width / 2, height / 2, -depth / 2),
        arrayOf(-width / 2, -height / 2, depth / 2),
        arrayOf(width / 2, -height / 2, depth / 2),
        arrayOf(-width / 2, height / 2, depth / 2),
        arrayOf(width / 2, height / 2, depth / 2),
    )
    val c = arrayOf(
        arrayOf(1f, 0.5f, 0.5f),
        arrayOf(0.5f, 0f, 0f),
        arrayOf(0.5f, 1f, 0.5f),
        arrayOf(0f, 0.5f, 0f),
        arrayOf(0.5f, 0.5f, 1f),
        arrayOf(0f, 0f, 0.5f)
    )
    val t = arrayOf(
        arrayOf(0f, 0f),
        arrayOf(1f, 0f),
        arrayOf(0f, 1f),
        arrayOf(1f, 1f)
    )
    geometry(gl) {
        attribute(POSITION, listOf(
            5, 1, 3, 5, 3, 7,
            0, 4, 6, 0, 6, 2,
            6, 7, 3, 6, 3, 2,
            0, 1, 5, 0, 5, 4,
            4, 5, 7, 4, 7, 6,
            1, 0, 2, 1, 2, 3
        ).map { i -> p[i] })
        attribute(COLOR_0, buildList {
            for (i in 0..5) {
                repeat(6) { add(c[i]) }
            }
        })
        attribute(TEXCOORD_0, buildList {
            repeat(6) {
                add(t[0])
                add(t[1])
                add(t[3])
                add(t[0])
                add(t[3])
                add(t[2])
            }
        })
    }
}
