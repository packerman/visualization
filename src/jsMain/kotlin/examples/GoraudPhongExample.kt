package examples

import common.*
import common.Attribute.Companion.attribute
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.html.HTMLCanvasElement

@Suppress("unused")
object GoraudPhongExample : Initializer<Application> {
    override fun initialize(gl: WebGL2RenderingContext): Application {

        val surface = ParametricSurface.sphere(
            radius = 0.5f,
            radiusSegments = 40,
            heightSegments = 40
        )

        val projectionMatrix = Matrix4()
        val modelViewMatrix = Matrix4()
        val normalMatrix = Matrix4()

        val lightDirection = Vector3(-0.25f, -0.25f, -0.25f)
        val lightAmbient = Vector4(0.03f, 0.03f, 0.03f, 1f)
        val lightDiffuse = Vector4(1f, 1f, 1f, 1f)
        val lightSpecular = Vector4(1f, 1f, 1f, 1f)

        val materialAmbient = Vector4(1f, 1f, 1f, 1f)
        val materialDiffuse = Vector4(46f / 256, 99f / 256, 191f / 256, 1f)
        val materialSpecular = Vector4(1f, 1f, 1f, 1f)
        val shininess = Vector1(10f)

        val clearColor = Vector4(0.9f, 0.9f, 0.9f, 1f)
        gl.clearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)

        val pipeline = Pipeline(
            mapOf(
                "a_position" to attribute(surface.positions),
                "a_normal" to attribute(surface.normals)
            ),
            uniformMap {
                +("u_LightDirection" to lightDirection)
                +("u_LightAmbient" to lightAmbient)
                +("u_LightDiffuse" to lightDiffuse)
                +("u_LightSpecular" to lightSpecular)
                +("u_MaterialAmbient" to materialAmbient)
                +("u_MaterialDiffuse" to materialDiffuse)
                +("u_MaterialSpecular" to materialSpecular)
                +("u_Shininess" to shininess)
                +("u_ModelViewMatrix" to modelViewMatrix)
                +("u_ProjectionMatrix" to projectionMatrix)
                +("u_NormalMatrix" to normalMatrix)
            },
            surface.indices,
            TRIANGLES,
            vertexShaderSource,
            fragmentShaderSource
        )

        gui("My Gui", 430) {
            color("Light Color", lightDiffuse) { it.copyTo(lightDiffuse) }
            number("Light Ambient Term", lightAmbient.x, 0f..1f,  0.01f) {
                lightAmbient.set(it, it, it, 1f)
            }
            number("Light Specular Term", lightSpecular.x, 0f..1f,  0.01f) {
                lightSpecular.set(it, it, it, 1f)
            }
            vector("Translate", lightDirection, -10f..10f, -0.1f) {
                lightDirection.set(-it.x, -it.y, it.z)
            }
            color("Sphere Color", materialDiffuse) { it.copyTo(materialDiffuse) }
            number("Material Ambient Term", materialAmbient.x, 0f..1f,  0.01f) {
                materialAmbient.set(it, it, it, 1f)
            }
            number("Material Specular Term", materialSpecular.x, 0f..1f,  0.01f) {
                materialSpecular.set(it, it, it, 1f)
            }
            number("Shininess", shininess.x, 0f..50f,  0.1f) {
                shininess.set(it)
            }
            color("Background", clearColor) {
                gl.clearColor(it.x, it.y, it.z, 1f)
            }
        }

        val renderable = pipeline.initialize(gl)

        return object : Application {
            var angle = 0f

            override fun update(elapsed: Double, keyState: KeyState) {
                angle += (90 * elapsed).toFloat() / 1000f
            }

            override fun render(gl: WebGL2RenderingContext) {
                val canvas = gl.canvas as HTMLCanvasElement
                gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT.toInt() or WebGL2RenderingContext.DEPTH_BUFFER_BIT.toInt())
                gl.viewport(0, 0, canvas.width, canvas.height)

                projectionMatrix.perspective(
                    toRadians(45f),
                    aspect(canvas.clientWidth, canvas.clientHeight),
                    0.1f,
                    1000f
                )
                modelViewMatrix.identity()
                    .translate(Vector3(0f, 0f, -1.5f))
                    .rotate(toRadians(angle), Vector3(0f, 1f, 0f))

                modelViewMatrix.copyTo(normalMatrix)
                    .invert()
                    ?.transpose()

                renderable.render(gl)
            }
        }
    }

    private val vertexShaderSource = """
    #version 300 es
    precision mediump float;
    
    in vec3 a_position;
    in vec3 a_normal;

    uniform mat4 u_ModelViewMatrix;
    uniform mat4 u_ProjectionMatrix;
    uniform mat4 u_NormalMatrix;
    
    uniform vec3 u_LightDirection;
    uniform vec4 u_LightAmbient;
    uniform vec4 u_LightDiffuse;
    uniform vec4 u_LightSpecular;
    
    uniform vec4 u_MaterialAmbient;
    uniform vec4 u_MaterialDiffuse;
    uniform vec4 u_MaterialSpecular;
    uniform float u_Shininess;

    out vec4 v_Color;

    void main(void) {
        vec4 vertex = u_ModelViewMatrix * vec4(a_position, 1.0);
      vec3 normal = normalize(vec3(u_NormalMatrix * vec4(a_normal, 1.0)));
      vec3 light = normalize(u_LightDirection);
      float lambertTerm = dot(normal, -light);
      
      vec4 ambient = u_LightAmbient * u_MaterialAmbient;
      vec4 diffuse = vec4(0.0, 0.0, 0.0, 1.0);
      vec4 specular = vec4(0.0, 0.0, 0.0, 1.0);
      
      if (lambertTerm > 0.0) {
        diffuse = u_LightDiffuse * u_MaterialDiffuse * lambertTerm;
        vec3 eye = -normalize(vertex.xyz);
        vec3 reflected = reflect(light, normal);
        float power = pow(max(dot(reflected, eye), 0.0), u_Shininess);
        specular = u_LightSpecular * u_MaterialSpecular * power;
      }
      
      v_Color = vec4(vec3(ambient + diffuse + specular), 1.0);
      gl_Position = u_ProjectionMatrix * vertex;
    }
        """.trimIndent()

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




