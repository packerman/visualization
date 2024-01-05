package examples.scene

import framework.core.*
import framework.geometry.Geometry.Companion.COLOR_0
import framework.geometry.Geometry.Companion.POSITION
import framework.geometry.geometry
import framework.material.basicMaterial
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

@Suppress("unused")
class CustomGeometry private constructor(
    private val renderer: Renderer,
    private val scene: Node,
    private val camera: Camera
) : Application {

    override fun render(gl: WebGL2RenderingContext) {
        renderer.render(gl, scene, camera)
    }

    companion object : Initializer<CustomGeometry> {

        override fun initialize(gl: WebGL2RenderingContext): CustomGeometry {
            val renderer = Renderer(gl, clearColor = Vector3(0.1f, 0.1f, 0.1f))
            val camera = Camera().apply {
                position = Vector3(0f, 0f, 1f)
            }
            val scene = scene(gl) {
                mesh(
                    geometry {
                        attribute(
                            POSITION, listOf(
                                arrayOf(-0.1f, 0.1f, 0f),
                                arrayOf(0f, 0f, 0f),
                                arrayOf(0.1f, 0.1f, 0f),
                                arrayOf(-0.2f, -0.2f, 0f),
                                arrayOf(0.2f, -0.2f, 0f)
                            )
                        )
                        attribute(
                            COLOR_0, listOf(
                                arrayOf(1f, 0f, 0f),
                                arrayOf(1f, 1f, 0f),
                                arrayOf(1f, 0f, 0f),
                                arrayOf(0f, 0.25f, 0f),
                                arrayOf(0f, 0.25f, 0f),
                            )
                        )
                        index(
                            arrayOf(
                                0, 1, 3, 1, 3, 4, 1, 4, 2
                            )
                        )
                    }, basicMaterial(
                        useVertexColors = true
                    )
                ) {
                    scale(3f)
                    translate(0f, 0.05f, 0f)
                }
            }
            return CustomGeometry(renderer, scene, camera)
        }
    }
}
