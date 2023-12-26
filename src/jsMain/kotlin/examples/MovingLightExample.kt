package examples

import common.*
import common.Attribute.Companion.attribute
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.html.HTMLCanvasElement

@Suppress("unused")
object MovingLightExample : Creator<Application> {
    override fun create(gl: WebGL2RenderingContext): Application {

        val surface = ParametricSurface.sphere(
            radius = 0.5f,
            radiusSegments = 40,
            heightSegments = 40
        )

        val projectionMatrix = Matrix4()
        val modelViewMatrix = Matrix4()
        val normalMatrix = Matrix4()

        val lightDirection = Vector3(0f, -1f, -1f)
        val lightDiffuse = Vector3(1f, 1f, 1f)
        val materialDiffuse = Vector3(0.5f, 0.8f, 0.1f)
        val pipeline = Pipeline(
            mapOf(
                "a_position" to attribute(surface.positions),
                "a_normal" to attribute(surface.normals)
            ),
            uniformMap {
                +("u_LightDirection" to lightDirection)
                +("u_LightDiffuse" to lightDiffuse)
                +("u_MaterialDiffuse" to materialDiffuse)
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
            color("Sphere Color", materialDiffuse) { it.copyTo(materialDiffuse) }
            color("Light Diffuse Color", lightDiffuse) { it.copyTo(lightDiffuse) }
            vector("Translate", lightDirection, -10f..10f, -0.1f) {
                lightDirection.set(-it.x, -it.y, it.z)
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
    uniform vec3 u_LightDiffuse;
    uniform vec3 u_MaterialDiffuse;

    out vec4 v_Color;

    void main(void) {
      vec3 normal = normalize(vec3(u_NormalMatrix * vec4(a_normal, 1.0)));
      vec3 light = normalize(vec3(u_ModelViewMatrix * vec4(u_LightDirection, 0.0)));
      float lambertTerm = dot(normal, -light);
      vec3 diffuse = u_MaterialDiffuse * u_LightDiffuse * lambertTerm;
      v_Color = vec4(diffuse, 1.0);
      gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(a_position, 1.0);
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




