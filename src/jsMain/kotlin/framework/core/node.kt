package framework.core

import framework.core.TransformType.Global
import framework.core.TransformType.Local
import framework.math.Matrix4
import framework.math.Matrix4.Companion.rotationX
import framework.math.Matrix4.Companion.rotationY
import framework.math.Matrix4.Companion.rotationZ
import framework.math.Matrix4.Companion.translation
import framework.math.Vector3
import framework.math.internal.getTranslation
import framework.math.internal.setTranslation

interface Node {
    var parent: Node?
    val children: List<Node>
    fun add(child: Node)
    fun remove(child: Node)
    val worldMatrix: Matrix4
    val descendants: Sequence<Node>
    fun applyMatrix(matrix: Matrix4, type: TransformType = Local)
    fun translate(x: Float, y: Float, z: Float, type: TransformType = Local)
    fun rotateX(angle: Float, type: TransformType = Local)
    fun rotateY(angle: Float, type: TransformType = Local)
    fun rotateZ(angle: Float, type: TransformType = Local)
    fun scale(s: Float, type: TransformType = Local)
    var position: Vector3
    val worldPosition: Vector3
}

class NodeImpl : Node {

    override var parent: Node? = null

    private val transform: Matrix4 = Matrix4.identity()
    private val _children: MutableList<Node> = mutableListOf()

    override val children: List<Node>
        get() = _children

    override fun add(child: Node) {
        _children.add(child)
        child.parent = this
    }

    override fun remove(child: Node) {
        _children.remove(child)
        child.parent = null
    }

    override val worldMatrix: Matrix4
        get() {
            val parent = this.parent
            return if (parent == null) transform else parent.worldMatrix * transform
        }

    override val descendants: Sequence<Node>
        get() = sequence {
            val toProcess = ArrayDeque<Node>()
            toProcess.addLast(this@NodeImpl)
            while (toProcess.isNotEmpty()) {
                val node = toProcess.removeFirst()
                yield(node)
                toProcess.addAll(node.children)
            }
        }

    override fun applyMatrix(matrix: Matrix4, type: TransformType) {
        when (type) {
            Local -> transform.timesAssign(matrix)
            Global -> transform.preMultiply(matrix)
        }
    }

    override fun translate(x: Float, y: Float, z: Float, type: TransformType) {
        applyMatrix(translation(x, y, z), type)
    }

    override fun rotateX(angle: Float, type: TransformType) {
        applyMatrix(rotationX(angle), type)
    }

    override fun rotateY(angle: Float, type: TransformType) {
        applyMatrix(rotationY(angle), type)
    }

    override fun rotateZ(angle: Float, type: TransformType) {
        applyMatrix(rotationZ(angle), type)
    }

    override fun scale(s: Float, type: TransformType) {
        applyMatrix(Matrix4.scale(s), type)
    }

    override var position: Vector3
        get() = Vector3(getTranslation(FloatArray(3), transform.floats))
        set(value) {
            setTranslation(transform.floats, value.floats)
        }

    override val worldPosition: Vector3
        get() = Vector3(getTranslation(FloatArray(3), worldMatrix.floats))
}

typealias Scene = NodeImpl

typealias Group = NodeImpl

enum class TransformType {
    Local,
    Global
}
