package examples

import common.Application
import common.Initializer
import common.Pipeline
import common.Vector3
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES

class ParametricSurface(
    val uRange: ClosedFloatingPointRange<Float>,
    val uResolution: Int,
    val vRange: ClosedFloatingPointRange<Float>,
    val vResolution: Int,
    val function: (u: Float, v: Float) -> Vector3
) {

    fun getPositions(): Array<Float> {
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
                    add(position.y)
                }
            }
        }
        return result.toTypedArray()
    }

    fun getNormals(): Array<Float> {
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

    fun getIndices(): Array<Short> {
        val result = ArrayList<Short>(6 * uResolution * vResolution).apply {
            for (uIndex in 0..<uResolution) {
                for (vIndex in 0..<vResolution) {
                    val i = getOffset(uIndex, vIndex)
                    val j = getOffset(uIndex, vIndex + 1)
                    val k = getOffset(uIndex + 1, vIndex)
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

    private fun getOffset(uIndex: Int, vIndex: Int): Int = uIndex * uResolution + vIndex

    companion object {
        private const val H = 0.0001f

        private fun getNormalTo(p0: Vector3, p1: Vector3, p2: Vector3): Vector3 =
            (p1 - p0).cross(p2 - p0).normalize()
    }
}

class GoraudLambertExample : Initializer<Application> {
    override fun initialize(gl: WebGL2RenderingContext): Application {
        val pipeline = Pipeline(
            mapOf(),
            mapOf(),
            arrayOf(),
            TRIANGLES,
            vertexShaderSource,
            fragmentShaderSource
        )

        TODO("Not yet implemented")
    }

    companion object {
        private val vertexShaderSource = """
    #version 300 es
    precision mediump float;
    
    in vec3 a_position;
    in vec3 a_normal;

    uniform mat4 u_ModelViewMatrix;
    uniform mat4 u_ProjectionMatrix;
    uniform mat4 u_NormalMatrix;
    uniform vec3 u_LightDirection;
    uniform vec3 u_LightDiffuse;
    uniform vec3 u_MaterialDiffuse;

    out vec4 v_Color;

    void main(void) {
      vec3 normal = normalize(vec3(u_NormalMatrix * vec4(a_Normal, 1.0)));
      vec3 light = normalize(u_LightDirection);
      float lambertTerm = dot(normal, -light);
      vec3 diffuse = u_MaterialDiffuse * u_LightDiffuse * lambertTerm;
      v_Color = vec4(diffuse, 1.0);
      gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(aVertexPosition, 1.0);
    }
        """.trimIndent()
    }

    private val fragmentShaderSource = """
            #version 300 es
        	precision mediump float;

            in vec4 v_Color;

            out vec4 fragColor;

            void main(void)  {
              fragColor = v_Color;
            }
    """.trimIndent()
}