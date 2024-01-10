package framework.material

import framework.core.Supplier
import web.gl.WebGL2RenderingContext

class NormalMaterial private constructor(private val material: Material) : Material by material {

    companion object {

        operator fun invoke(
            gl: WebGL2RenderingContext,
        ): NormalMaterial = NormalMaterial(MaterialImpl(gl, VERTEX_SHADER, FRAGMENT_SHADER, doubleSided = false))

        private const val VERTEX_SHADER = """
            in vec4 a_position;
            in vec3 a_normal;
    
            uniform mat4 u_ProjectionMatrix;
            uniform mat4 u_ViewMatrix;
            uniform mat4 u_ModelMatrix;
           
            out vec3 v_Normal;
    
            void main() {
                gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * a_position;
                v_Normal = mat3(u_ModelMatrix) * a_normal;
            }
        """

        private const val FRAGMENT_SHADER = """
            in vec3 v_Normal;
            
            out vec4 fragColor;
            
            void main() {
                fragColor = vec4((normalize(v_Normal) + vec3(1, 1, 1))/2.0, 1);
            }
        """
    }
}

fun normal(): Supplier<NormalMaterial> = { gl: WebGL2RenderingContext -> NormalMaterial.invoke(gl) }
