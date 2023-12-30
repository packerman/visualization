package framework.math

import kotlin.math.acos

private val PI = acos(-1f)

fun toRadians(degrees: Float): Float = degrees * PI / 180f

fun aspect(a: Int, b: Int): Float = a.toFloat() / b.toFloat()
