package examples.scene

import framework.core.*
import framework.geometry.Geometry.Companion.POSITION
import framework.geometry.Mode.LineStrip
import framework.geometry.Mode.Points
import framework.geometry.geometry
import framework.material.basicMaterial
import framework.math.Vector3
import framework.math.aRange
import web.gl.WebGL2RenderingContext
import kotlin.math.sin

@Suppress("unused")
class Sine private constructor(
    private val renderer: Renderer,
    private val scene: Node,
    private val camera: Camera,
) : Application {

    override fun update(elapsed: Double, keyState: KeyState) {
    }

    override fun render(gl: WebGL2RenderingContext) {
        renderer.render(gl, scene, camera)
    }

    companion object : Initializer<Sine> {

        override fun initialize(gl: WebGL2RenderingContext): Sine {
            val renderer = Renderer(gl, clearColor = Vector3(0f, 0f, 0f))
            val camera = Camera().apply {
                position = Vector3(0f, 0f, 4f)
            }

            val sine = geometry {
                attribute(POSITION, aRange(-3.2f, 3.2f, 0.3f)
                    .map { x -> arrayOf(x, sin(x), 0f) }
                    .toList())
            }

            val scene = scene(gl) {
                mesh(
                    sine, basicMaterial(
                        baseColor = Vector3(1f, 1f, 0f),
                        pointSize = 4f
                    ),
                    mode = Points
                )
                mesh(
                    sine, basicMaterial(
                        baseColor = Vector3(1f, 0f, 1f)
                    ), mode = LineStrip
                )
            }

            return Sine(renderer, scene, camera)
        }
    }
}
