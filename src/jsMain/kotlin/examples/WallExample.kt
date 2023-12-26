package examples

import common.*
import common.Attribute.Companion.attribute
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.html.HTMLCanvasElement
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
object WallExample : Creator<Application> {
    override fun create(gl: WebGL2RenderingContext): Application {

        val positions = arrayOf(
            -20f, -8f, 20f,
            -10f, -8f, 0f,
            10f, -8f, 0f,
            20f, -8f, 20f,
            -20f, 8f, 20f,
            -10f, 8f, 0f,
            10f, 8f, 0f,
            20f, 8f, 20f
        )
        val indices = arrayOf<Short>(
            0, 5, 4,
            1, 5, 0,
            1, 6, 5,
            2, 6, 1,
            2, 7, 6,
            3, 7, 2
        )
        val normals = calculateNormals(positions, indices)

        val clearColor = Vector4(0.9f, 0.9f, 0.9f, 1f)
        gl.clearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)

        val uniforms = uniformMap {
            uniform("u_LightDirection", Vector3(0f, 0f, -1f))
            uniform("u_LightAmbient", Vector4(0.01f, 0.01f, 0.01f, 1f))
            uniform("u_LightDiffuse", Vector4(0.5f, 0.5f, 0.5f, 1f))
            uniform("u_MaterialDiffuse", Vector4(0.1f, 0.5f, 0.8f, 1f))
            uniform("u_ModelViewMatrix", Matrix4())
            uniform("u_ProjectionMatrix", Matrix4())
            uniform("u_NormalMatrix", Matrix4())
        }
        val pipeline = Pipeline(
            mapOf(
                "a_position" to attribute(positions),
                "a_normal" to attribute(normals)
            ),
            uniforms,
            indices,
            TRIANGLES,
            vertexShaderSource,
            fragmentShaderSource
        )

        val renderable = pipeline.initialize(gl)

        return object : Application {
            var elevation = 0f
            var azimuth = 0f
            val incrementValue = 2f

            override fun update(elapsed: Double, keyState: KeyState) {
                if (keyState.isPressed("ArrowLeft")) {
                    azimuth -= incrementValue
                }
                if (keyState.isPressed("ArrowUp")) {
                    elevation += incrementValue
                }
                if (keyState.isPressed("ArrowRight")) {
                    azimuth += incrementValue
                }
                if (keyState.isPressed("ArrowDown")) {
                    elevation -= incrementValue
                }
                azimuth %= 360f
                elevation %= 360f
                val theta = toRadians(elevation)
                val phi = toRadians(azimuth)
                uniforms.get<Vector3>("u_LightDirection").set(
                    cos(theta) * sin(phi),
                    sin(theta),
                    cos(theta) * -cos(phi)
                )
            }

            override fun render(gl: WebGL2RenderingContext) {
                val canvas = gl.canvas as HTMLCanvasElement
                gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT.toInt() or WebGL2RenderingContext.DEPTH_BUFFER_BIT.toInt())
                gl.viewport(0, 0, canvas.width, canvas.height)

                uniforms["u_ProjectionMatrix", Matrix4::class].perspective(
                    toRadians(45f),
                    aspect(canvas.clientWidth, canvas.clientHeight),
                    0.1f,
                    10000f
                )
                uniforms.get<Matrix4>("u_ModelViewMatrix").identity()
                    .translate(Vector3(0f, 0f, -40f))

                uniforms.get<Matrix4>("u_ModelViewMatrix").copyTo(uniforms["u_NormalMatrix"])
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
    
    uniform vec4 u_MaterialDiffuse;

    out vec4 v_Color;

    void main(void) {
      vec4 vertex = u_ModelViewMatrix * vec4(a_position, 1.0);
      vec3 normal = normalize(vec3(u_NormalMatrix * vec4(a_normal, 1.0)));
      vec3 light = normalize(u_LightDirection);
      float lambertTerm = dot(normal, -light);
      
      vec4 ambient = u_LightAmbient;
      vec4 diffuse = u_MaterialDiffuse * u_LightDiffuse * lambertTerm;
      
      v_Color = vec4(vec3(ambient + diffuse), 1.0);
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




