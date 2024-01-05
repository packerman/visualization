package framework.material

import framework.core.Sampler2D
import framework.core.Supplier
import framework.core.Texture
import framework.core.uniformMap
import framework.math.Vector2
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

class TextureMaterial private constructor(private val material: Material) : Material by material {

    companion object {

        operator fun invoke(
            gl: WebGL2RenderingContext,
            texture: Sampler2D,
            baseColor: Vector3,
            repeatUV: Vector2,
            offsetUV: Vector2,
        ): TextureMaterial = TextureMaterial(MaterialImpl(gl, VERTEX_SHADER, FRAGMENT_SHADER, uniformMap {
            uniform(TEXTURE, texture)
            uniform(BASE_COLOR, baseColor)
            uniform(REPEAT_UV, repeatUV)
            uniform(OFFSET_UV, offsetUV)
        }, doubleSided = true))

        private const val REPEAT_UV = "u_RepeatUV"
        private const val OFFSET_UV = "u_OffsetUV"
        private const val BASE_COLOR = "u_BaseColor"
        private const val TEXTURE = "u_Texture"

        private const val VERTEX_SHADER = """
            in vec4 a_position;
            in vec2 a_texcoord_0;
    
            uniform mat4 u_ProjectionMatrix;
            uniform mat4 u_ViewMatrix;
            uniform mat4 u_ModelMatrix;
            
            uniform vec2 u_RepeatUV;
            uniform vec2 u_OffsetUV;
    
            out vec2 v_Texcoord_0;
    
            void main() {
        
                gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * a_position;
                v_Texcoord_0 = a_texcoord_0 * u_RepeatUV + u_OffsetUV;
            }
        """

        private const val FRAGMENT_SHADER = """
            uniform vec3 u_BaseColor;
            uniform sampler2D u_Texture;
            
            in vec2 v_Texcoord_0;
            
            out vec4 fragColor;
            
            void main() {
                vec4 color = vec4(u_BaseColor, 1.0) * texture(u_Texture, v_Texcoord_0);
                if (color.a < 0.1)
                    discard;
                fragColor = color;
            }
        """
    }
}

fun textureMaterial(
    texture: Supplier<Texture>,
    baseColor: Vector3 = Vector3(1f, 1f, 1f),
    repeatUV: Vector2 = Vector2(1f, 1f),
    offsetUV: Vector2 = Vector2(0f, 0f)
): Supplier<TextureMaterial> = { gl ->
    TextureMaterial(gl, texture(gl).getSampler2D(0), baseColor, repeatUV, offsetUV)
}
