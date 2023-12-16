package common

import kotlin.math.PI

const val PI_FLOAT = PI.toFloat()

fun toRadians(degrees: Float): Float = degrees * PI_FLOAT / 180f

fun aspect(a: Int, b: Int): Float = a.toFloat() / b.toFloat()