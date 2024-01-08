package framework.material

import framework.core.Sampler2D
import framework.core.Supplier
import framework.core.Texture
import framework.core.uniformMap
import framework.light.LightType.Companion.getGlslDeclarations
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

class FlatMaterial private constructor(private val material: Material) : Material by material {

    companion object {

        operator fun invoke(
            gl: WebGL2RenderingContext,
            texture: Sampler2D?,
            baseColor: Vector3 = Vector3(1f, 1f, 1f),
        ): FlatMaterial = FlatMaterial(MaterialImpl(gl, VERTEX_SHADER, FRAGMENT_SHADER, uniformMap {
            if (texture != null) {
                uniform(USE_TEXTURE, true)
                uniform(TEXTURE, texture)
            } else {
                uniform(USE_TEXTURE, false)
            }
            uniform(BASE_COLOR, baseColor)
        }, doubleSided = true))

        private const val USE_TEXTURE = "u_UseTexture"
        private const val BASE_COLOR = "u_BaseColor"
        private const val TEXTURE = "u_Texture"

        private val VERTEX_SHADER = """
            ${getGlslDeclarations()}
            
            struct Light {
                int type;
                vec3 color;
                vec3 direction;
                vec3 position;
                vec3 attenuation;
            };
            
            in vec4 a_position;
            in vec2 a_texcoord_0;
            in vec3 a_normal;
    
            uniform mat4 u_ProjectionMatrix;
            uniform mat4 u_ViewMatrix;
            uniform mat4 u_ModelMatrix;
            
            uniform Light lights[4];
            uniform int lightCount;
            
            float attenuationCalc(vec3 attenuation, float x) {
                return 1.0 / (attenuation[0] + attenuation[1]*x + attenuation[2]*x*x);
            }
            
            vec3 lightCalc(Light light, vec3 position, vec3 normal) {
                float ambient = 0.0;
                float diffuse = 0.0;
                float attenuation = 1.0;
                vec3 direction = vec3(0, 0, 0);
                
                if (light.type == AMBIENT) {
                    ambient = 1.0;
                } else if (light.type == DIRECTIONAL) {
                    vec3 direction = normalize(light.direction);
                } else if (light.type == POINT)  {
                    direction = normalize(position - light.position);
                    float distance = length(light.position - position);
                    attenuation = attenuationCalc(light.attenuation, distance);
                }
                if (light.type != AMBIENT) {
                    normal = normalize(normal);
                    diffuse = max(dot(normal, - direction), 0.0);
                    diffuse *= attenuation;
                }
                return light.color * (ambient + diffuse);
            }
            
            out vec2 v_Texcoord_0;
            out vec3 light;
    
            void main() {
                gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * a_position;
                v_Texcoord_0 = a_texcoord_0;
                vec3 position = vec3(u_ModelMatrix * a_position);
                vec3 normal = normalize(mat3(u_ModelMatrix) * a_normal);
                light = vec3(0, 0, 0);
                for (int i = 0; i < lightCount; i++) {
                    light += lightCalc(lights[i], position, normal);
                }
            }
        """

        private const val FRAGMENT_SHADER = """
            uniform vec3 u_BaseColor;
            uniform bool u_UseTexture;
            uniform sampler2D u_Texture;
            
            in vec2 v_Texcoord_0;
            in vec3 light;
            
            out vec4 fragColor;
            
            void main() {
                vec4 color = vec4(u_BaseColor, 1.0);
                if (u_UseTexture) {
                    color *= texture(u_Texture, v_Texcoord_0);
                }
                color *= vec4(light, 1);
                fragColor = color;
            }
        """
    }
}

fun flat(
    texture: Supplier<Texture>?,
    baseColor: Vector3 = Vector3(1f, 1f, 1f),
): Supplier<FlatMaterial> = { gl ->
    FlatMaterial(gl, texture?.invoke(gl)?.getSampler2D(0), baseColor)
}
