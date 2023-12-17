package examples

import common.*
import common.Attribute.Companion.attribute
import common.Uniform.Companion.uniform
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.html.HTMLCanvasElement
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
object WallExample : Initializer<Application> {
    override fun initialize(gl: WebGL2RenderingContext): Application {

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

        val projectionMatrix = Matrix4()
        val modelViewMatrix = Matrix4()
        val normalMatrix = Matrix4()

        val lightDirection = Vector3(0f, 0f, -1f)
        val lightAmbient = Vector4(0.01f, 0.01f, 0.01f, 1f)
        val lightDiffuse = Vector4(0.5f, 0.5f, 0.5f, 1f)

        val materialDiffuse = Vector4(0.1f, 0.5f, 0.8f, 1f)

        val clearColor = Vector4(0.9f, 0.9f, 0.9f, 1f)
        gl.clearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)

        val pipeline = Pipeline(
            mapOf(
                "a_position" to attribute(positions),
                "a_normal" to attribute(normals)
            ),
            mapOf(
                "u_LightDirection" to uniform(lightDirection),
                "u_LightAmbient" to uniform(lightAmbient),
                "u_LightDiffuse" to uniform(lightDiffuse),
                "u_MaterialDiffuse" to uniform(materialDiffuse),
                "u_ModelViewMatrix" to uniform(modelViewMatrix),
                "u_ProjectionMatrix" to uniform(projectionMatrix),
                "u_NormalMatrix" to uniform(normalMatrix),
            ),
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
                lightDirection.set(
                    cos(theta) * sin(phi),
                    sin(theta),
                    cos(theta) * -cos(phi)
                )
            }

            override fun render(gl: WebGL2RenderingContext) {
                val canvas = gl.canvas as HTMLCanvasElement
                gl.clear(WebGL2RenderingContext.COLOR_BUFFER_BIT.toInt() or WebGL2RenderingContext.DEPTH_BUFFER_BIT.toInt())
                gl.viewport(0, 0, canvas.width, canvas.height)

                projectionMatrix.perspective(
                    toRadians(45f),
                    aspect(canvas.clientWidth, canvas.clientHeight),
                    0.1f,
                    10000f
                )
                modelViewMatrix.identity()
                    .translate(Vector3(0f, 0f, -40f))

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




