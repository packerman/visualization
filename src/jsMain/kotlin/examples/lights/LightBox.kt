package examples.lights

import framework.core.*
import framework.geometry.boxGeometry
import framework.light.Light.Companion.directional
import framework.material.flat
import framework.math.Vector3
import framework.math.toRadians
import web.gl.WebGL2RenderingContext

@Suppress("unused")
class LightBox private constructor(
    private val renderer: Renderer,
    private val scene: Scene,
    private val camera: Camera,
    private val mesh: Mesh
) : Application {

    override fun update(elapsed: Double, keyState: KeyState) {
    }

    override fun render(gl: WebGL2RenderingContext) {
        renderer.render(gl, scene, camera)
    }

    companion object : Initializer<LightBox> {

        override fun initialize(gl: WebGL2RenderingContext): LightBox {
            val renderer = Renderer(gl, clearColor = Vector3(0.9f, 0.9f, 0.9f))
            val camera = Camera().apply {
                rotateY(toRadians(15f))
                rotateX(toRadians(-15f))
                position = Vector3(1f, 1f, 2f)
            }
            val mesh = mesh(
                gl, boxGeometry(), flat(
                    loadTexture("textures/crate.png")
                )
            )
            val scene = Scene().apply {
                add(mesh)
                add(
                    directional(
                        color = Vector3(0.5f, 0.5f, 0.5f),
                        direction = Vector3(-3f, -2f, -1f)
                    )
                )
            }
            return LightBox(renderer, scene, camera, mesh)
        }
    }
}
