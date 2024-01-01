package examples.scene

import framework.core.*
import framework.geometry.boxGeometry
import framework.material.basicMaterial
import framework.math.Vector3
import framework.math.toRadians
import web.gl.WebGL2RenderingContext

@Suppress("unused")
class SpinningCube private constructor(
    private val renderer: Renderer,
    private val scene: Scene,
    private val camera: Camera,
    private val mesh: Mesh
) : Application {

    val yRotateSpeed = toRadians(120f)
    val xRotateSpeed = toRadians(90f)

    override fun update(elapsed: Double, keyState: KeyState) {
        mesh.rotateY(elapsed.toFloat() * yRotateSpeed / 1000f)
        mesh.rotateX(elapsed.toFloat() * xRotateSpeed / 1000f)
    }

    override fun render(gl: WebGL2RenderingContext) {
        renderer.render(gl, scene, camera)
    }

    companion object : Initializer<SpinningCube> {

        override fun initialize(gl: WebGL2RenderingContext): SpinningCube {
            val renderer = Renderer(gl, clearColor = Vector3(0.9f, 0.9f, 0.9f))
            val scene = NodeImpl()
            val camera = Camera()
            camera.position = Vector3(0f, 0f, 2f)

            val mesh = mesh(gl, boxGeometry(), basicMaterial {
                useVertexColors = true
            })

            scene.add(mesh)

            return SpinningCube(renderer, scene, camera, mesh)
        }
    }
}
