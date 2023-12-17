package common

import kotlin.math.PI

const val DEGREES_TO_RADIANS = PI.toFloat() / 180f

fun toRadians(degrees: Float): Float = degrees * DEGREES_TO_RADIANS

fun aspect(a: Int, b: Int): Float = a.toFloat() / b.toFloat()