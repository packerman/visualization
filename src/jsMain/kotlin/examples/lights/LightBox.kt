package examples.lights

import framework.core.*
import framework.geometry.boxGeometry
import framework.light.Light
import framework.light.Light.Companion.ambient
import framework.light.Light.Companion.directional
import framework.material.flat
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

@Suppress("unused")
class LightBox private constructor(
    private val renderer: Renderer,
    private val scene: Node,
    private val camera: Camera,
    private val light: Light
) : Application {

    private var position = Vector3(0f, 0f, 0f)
    private var angleY = 0f
    private var angleX = 0f
    private var lightDirection = Vector3(-1f, -1f, -1f)

    override fun start(gl: WebGL2RenderingContext) {
        gui {
            folder("Camera") {
                number("Rotate Y", 0f, -3.14f..3.14f, 0.01f) {
                    angleY = it
                }
                number("Rotate X", 0f, -3.14f..3.14f, 0.01f) {
                    angleX = it
                }
                vector("Position", position, -5f..5f, 0.01f) {
                    position = it
                }
            }
            folder("Light") {
                vector("Direction", lightDirection, -10f..10f, 0.01f) {
                    lightDirection = it
                }
            }
        }
    }

    override fun update(elapsed: Double, keyState: KeyState) {
        camera.direction = Vector3(0f, 0f, -1f)
        camera.rotateY(angleY)
        camera.rotateX(angleX)
        camera.position = position
        light.direction = lightDirection
    }

    override fun render(gl: WebGL2RenderingContext) {
        renderer.render(gl, scene, camera)
    }

    companion object : Initializer<LightBox> {

        override fun initialize(gl: WebGL2RenderingContext): LightBox {
            val renderer = Renderer(gl, clearColor = Vector3(0.9f, 0.9f, 0.9f))
            val camera = Camera()
            val mesh = mesh(
                gl, boxGeometry(), flat(
                    loadTexture("textures/crate.png")
                )
            )
            val light = directional()
            val scene = scene(gl) {
                add(mesh)
                add(ambient(Vector3(0.1f, 0.1f, 0.1f)))
                add(light)
            }
            return LightBox(renderer, scene, camera, light)
        }
    }
}
