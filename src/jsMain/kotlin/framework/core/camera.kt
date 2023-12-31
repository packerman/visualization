package framework.core

import framework.math.Matrix4
import framework.math.toRadians

class Camera(
    override val node: NodeImpl,
    private val angleOfView: Float = toRadians(60f),
    private val near: Float = 0.1f,
    private val far: Float = 1000f
) : Node<NodeImpl> by node, WithNode {

    var aspectRatio: Float = 1f
        set(value) {
            field = value
            Matrix4.perspective(projectionMatrix, angleOfView, value, near, far)
        }

    val projectionMatrix = Matrix4.perspective(angleOfView, aspectRatio, near, far)
    val viewMatrix = Matrix4.identity()

    fun updateViewMatrix() {
        worldMatrix.invert(result = viewMatrix)
    }

    companion object {
        operator fun invoke(angleOfView: Float = toRadians(60f), near: Float = 0.1f, far: Float = 1000f) =
            Camera(NodeImpl(), angleOfView, near, far)
    }
}
