package examples

import common.*
import common.Attribute.Companion.attribute
import common.Uniform.Companion.uniform
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.html.HTMLCanvasElement

object GoraudLambertExample : Initializer<Application> {
    override fun initialize(gl: WebGL2RenderingContext): Application {

        val surface = ParametricSurface.sphere(
            radius = 0.5f,
            radiusSegments = 40,
            heightSegments = 40
        )

        val projectionMatrix = Matrix4()
        val modelViewMatrix = Matrix4()
        val normalMatrix = Matrix4()

        val pipeline = Pipeline(
            mapOf(
                "a_position" to attribute(surface.positions),
                "a_normal" to attribute(surface.normals)
            ),
            mapOf(
                "u_LightDirection" to uniform(0f, -1f, -1f),
                "u_LightDiffuse" to uniform(1f, 1f, 1f),
                "u_MaterialDiffuse" to uniform(0.5f, 0.8f, 0.1f),
                "u_ModelViewMatrix" to uniform(modelViewMatrix),
                "u_ProjectionMatrix" to uniform(projectionMatrix),
                "u_NormalMatrix" to uniform(normalMatrix),
            ),
            surface.indices,
            TRIANGLES,
            vertexShaderSource,
            fragmentShaderSource
        )

        val renderable = pipeline.initialize(gl)

        return object : Application {
            override fun draw(gl: WebGL2RenderingContext) {
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
      vec3 light = normalize(u_LightDirection);
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




