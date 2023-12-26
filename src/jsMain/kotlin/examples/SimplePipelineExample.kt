package examples

import common.Application
import common.Attribute.Companion.attribute
import common.Creator
import common.Pipeline
import common.uniformMap
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.COLOR_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.DEPTH_BUFFER_BIT
import web.gl.WebGL2RenderingContext.Companion.TRIANGLES
import web.html.HTMLCanvasElement

@Suppress("unused")
object SimplePipelineExample : Creator<Application> {

    override fun create(gl: WebGL2RenderingContext): Application {
        val pipeline = Pipeline(
            mapOf("aVertexPosition" to attribute(
                arrayOf(
                    -0.5f, 0.5f, 0f,
                    -0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0.5f, 0.5f, 0f
                )
            ),
                "aColor" to attribute(
                    arrayOf(
                        1f, 0f, 0f, 1f,
                        0f, 1f, 0f, 1f,
                        0f, 0f, 1f, 1f,
                        1f, 0f, 1f, 1f
                    )
                )
            ),
            uniformMap {  },
            arrayOf(0, 1, 2, 0, 2, 3),
            TRIANGLES,
            """
            #version 300 es
            precision mediump float;

            // Supplied vertex position attribute
            in vec3 aVertexPosition;
            in vec4 aColor;
            
            out vec4 v_Color;

            void main(void) {
                gl_Position = vec4(aVertexPosition, 1.0);
                gl_PointSize = 10.0;
                v_Color = aColor;
            }
            """.trimIndent(),
            """
            #version 300 es
            precision mediump float;

            in vec4 v_Color;
            out vec4 fragColor;

            void main(void) {
                fragColor = v_Color;
            }
            """.trimIndent()
        )

        val renderable = pipeline.initialize(gl)

        return object : Application {
            override fun render(gl: WebGL2RenderingContext) {
                gl.clear(COLOR_BUFFER_BIT.toInt() or DEPTH_BUFFER_BIT.toInt())
                gl.viewport(0, 0, (gl.canvas as HTMLCanvasElement).width, (gl.canvas as HTMLCanvasElement).height)

                renderable.render(gl)
            }
        }
    }
}
