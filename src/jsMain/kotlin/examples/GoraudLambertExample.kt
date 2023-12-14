package examples

import common.*
import common.Attribute.Companion.attribute
import common.Uniform.Companion.uniform
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.html.HTMLCanvasElement

object GoraudLambertExample : Initializer<Application> {
    override fun initialize(gl: WebGL2RenderingContext): Application {

        val sphere = Sphere(
            radius = 0.9f
        )

        console.log("Position count: " + sphere.positions.size)
        console.log("Position normals: " + sphere.normals.size)
        console.log("Indices: " + sphere.indices.size)

        val pipeline = Pipeline(
            mapOf(
                "a_position" to attribute(sphere.positions),
                "a_normal" to attribute(sphere.normals)
            ),
            mapOf(
                "u_LightDirection" to uniform(0f, -1f, -1f),
                "u_LightDiffuse" to uniform(1f, 1f, 1f),
                "u_MaterialDiffuse" to uniform(0.5f, 0.8f, 0.1f),
                "u_ModelViewMatrix" to uniform(Matrix4.identity()),
                "u_ProjectionMatrix" to uniform(Matrix4.identity()),
                "u_NormalMatrix" to uniform(Matrix4.identity()),
            ),
            sphere.indices,
            TRIANGLES,
            vertexShaderSource,
            fragmentShaderSource
        )

        val renderable = pipeline.initialize(gl)

        return object : Application {
            override fun draw(gl: WebGL2RenderingContext) {
                gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT.toInt() or WebGL2RenderingContext.DEPTH_BUFFER_BIT.toInt())
                gl.viewport(0, 0, (gl.canvas as HTMLCanvasElement).width, (gl.canvas as HTMLCanvasElement).height)

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




