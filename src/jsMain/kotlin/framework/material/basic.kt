package framework.material

import framework.core.Supplier
import framework.core.uniformMap
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

class BasicMaterial private constructor(private val material: Material) : Material by material {

    var baseColor: Vector3
        get() = getUniform(BASE_COLOR)
        set(value) {
            setUniform(BASE_COLOR, value)
        }

    var useVertexColors: Boolean
        get() = getUniform(USE_VERTEX_COLORS)
        set(value) {
            setUniform(USE_VERTEX_COLORS, value)
        }

    companion object {
        operator fun invoke(
            gl: WebGL2RenderingContext,
            baseColor: Vector3,
            useVertexColors: Boolean,
            pointSize: Float,
            doubleSided: Boolean
        ): BasicMaterial = BasicMaterial(
            MaterialImpl(
                gl, vertexShader(pointSize), FRAGMENT_SHADER, uniformMap {
                    uniform(BASE_COLOR, baseColor)
                    uniform(USE_VERTEX_COLORS, useVertexColors)
                },
                doubleSided
            )
        )

        private const val BASE_COLOR = "u_BaseColor"
        private const val USE_VERTEX_COLORS = "u_UseVertexColor"

        private fun vertexShader(pointSize: Float) = """
    in vec4 a_position;
    in vec4 a_color_0;
    
    uniform mat4 u_ProjectionMatrix;
    uniform mat4 u_ViewMatrix;
    uniform mat4 u_ModelMatrix;
    
    out vec4 v_Color;
    
    void main() {
        gl_PointSize = float($pointSize);
        gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * a_position;
        v_Color = a_color_0;
    }
"""

        private const val FRAGMENT_SHADER = """
    in vec4 v_Color;
    
    uniform vec3 u_BaseColor;
    uniform bool u_UseVertexColor;
    
    out vec4 fragColor;
    
    void main() {
        vec4 tempColor = vec4(u_BaseColor, 1.0);
        if (u_UseVertexColor) {
            tempColor *= v_Color;
        }
        fragColor = tempColor;
    }
"""
    }
}

fun basicMaterial(
    baseColor: Vector3 = Vector3(1f, 1f, 1f),
    useVertexColors: Boolean = false,
    pointSize: Float = 1f,
    doubleSided: Boolean = false
): Supplier<BasicMaterial> = { gl ->
    BasicMaterial(gl, baseColor, useVertexColors, pointSize, doubleSided)
}
