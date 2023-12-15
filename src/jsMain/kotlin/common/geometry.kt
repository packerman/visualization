package common

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ParametricSurface(
    val uRange: ClosedFloatingPointRange<Float>,
    val uResolution: Int,
    val vRange: ClosedFloatingPointRange<Float>,
    val vResolution: Int,
    val function: (u: Float, v: Float) -> Vector3
) {

    val positions: Array<Float>
        get() {
            val uDelta = (uRange.endInclusive - uRange.start) / uResolution
            val vDelta = (vRange.endInclusive - vRange.start) / vResolution

            val result = ArrayList<Float>(3 * (uResolution + 1) * (vResolution + 1)).apply {
                for (uIndex in 0..uResolution) {
                    for (vIndex in 0..vResolution) {
                        val u = uRange.start + uIndex * uDelta
                        val v = vRange.start + vIndex * vDelta
                        val position = function(u, v)
                        add(position.x)
                        add(position.y)
                        add(position.z)
                    }
                }
            }
            return result.toTypedArray()
        }

    val normals: Array<Float>
        get() {
            val uDelta = (uRange.endInclusive - uRange.start) / uResolution
            val vDelta = (vRange.endInclusive - vRange.start) / vResolution
            val result = ArrayList<Float>(3 * (uResolution + 1) * (vResolution + 1)).apply {
                for (uIndex in 0..uResolution) {
                    for (vIndex in 0..vResolution) {
                        val u = uRange.start + uIndex * uDelta
                        val v = vRange.start + vIndex * vDelta
                        val p0 = function(u, v)
                        val p1 = function(u + H, v)
                        val p2 = function(u, v + H)
                        val normal = getNormalTo(p0, p1, p2)
                        add(normal.x)
                        add(normal.y)
                        add(normal.z)
                    }
                }
            }
            return result.toTypedArray()
        }

    val indices: Array<Short>
        get() {
            val result = ArrayList<Short>(6 * uResolution * vResolution).apply {
                for (uIndex in 0..<uResolution) {
                    for (vIndex in 0..<vResolution) {
                        val i = getOffset(uIndex, vIndex)
                        val j = getOffset(uIndex + 1, vIndex)
                        val k = getOffset(uIndex, vIndex + 1)
                        val l = getOffset(uIndex + 1, vIndex + 1)
                        add(i.toShort())
                        add(j.toShort())
                        add(k.toShort())
                        add(k.toShort())
                        add(j.toShort())
                        add(l.toShort())
                    }
                }
            }
            return result.toTypedArray()
        }

    private fun getOffset(uIndex: Int, vIndex: Int): Int = uIndex * (vResolution + 1) + vIndex

    companion object {
        private const val H = 0.0001f

        fun sphere(radius: Float = 1f, radiusSegments: Int = 32, heightSegments: Int = 16) =
            ParametricSurface(
                0f..2f * PI.toFloat(),
                radiusSegments,
                -PI.toFloat() / 2f..PI.toFloat() / 2f,
                heightSegments
            ) { u, v -> Vector3(radius * sin(u) * cos(v), radius * sin(v), radius * cos(u) * cos(v)) }

        fun plane(
            origin: Vector3, uEdge: Vector3, vEdge: Vector3,
            uResolution: Int = 1, vResolution: Int = 1
        ) =
            ParametricSurface(
                0f..1f,
                uResolution,
                0f..1f,
                vResolution
            ) { u, v -> origin + uEdge * u + vEdge * v }

        private fun getNormalTo(p0: Vector3, p1: Vector3, p2: Vector3): Vector3 =
            (p1 - p0).cross(p2 - p0).normalize()
    }
}
