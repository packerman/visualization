package examples.scene

import framework.core.*
import framework.geometry.boxGeometry
import framework.material.BasicMaterial
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

@Suppress("unused")
class SpinningCube private constructor(
    private val renderer: Renderer,
    private val scene: Scene,
    private val camera: Camera,
    private val mesh: Mesh
) : Application {

    override fun update(elapsed: Double, keyState: KeyState) {
        mesh.rotateY(0.0514f)
        mesh.rotateX(0.0337f)
    }

    override fun render(gl: WebGL2RenderingContext) {
        renderer.render(gl, scene, camera)
    }

    companion object : Initializer<SpinningCube> {

        override fun initialize(gl: WebGL2RenderingContext): SpinningCube {
            val renderer = Renderer(gl, clearColor = Vector3(0.9f, 0.9f, 0.9f))
            val scene = NodeImpl()
            val camera = Camera()
            camera.position = Vector3(0f, 0f, 4f)

            val mesh = Mesh(gl, boxGeometry(gl), BasicMaterial(gl).apply {
                useVertexColors = true
            })
            scene.add(mesh)

            return SpinningCube(renderer, scene, camera, mesh)
        }
    }
}
