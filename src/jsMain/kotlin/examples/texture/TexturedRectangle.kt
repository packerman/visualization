package examples.texture

import framework.core.*
import framework.geometry.rectangleGeometry
import framework.material.textureMaterial
import framework.math.Vector3
import web.gl.WebGL2RenderingContext

@Suppress("unused")
class TexturedRectangle private constructor(
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

    companion object : Initializer<TexturedRectangle> {

        override fun initialize(gl: WebGL2RenderingContext): TexturedRectangle {
            val renderer = Renderer(gl, clearColor = Vector3(0.9f, 0.9f, 0.9f))
            val camera = Camera().apply {
                position = Vector3(0f, 0f, 2f)
            }
            val mesh = mesh(
                gl, rectangleGeometry(), textureMaterial(
                    loadTexture("textures/bloc.jpg"),
                )
            )
            val scene = Scene().apply {
                add(mesh)
            }
            return TexturedRectangle(renderer, scene, camera, mesh)
        }
    }
}
