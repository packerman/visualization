package framework.core

import framework.core.TransformType.Global
import framework.core.TransformType.Local
import framework.geometry.Geometry
import framework.geometry.Mode
import framework.material.Material
import framework.math.Matrix3
import framework.math.Matrix4
import framework.math.Matrix4.Companion.rotationX
import framework.math.Matrix4.Companion.rotationY
import framework.math.Matrix4.Companion.rotationZ
import framework.math.Matrix4.Companion.translation
import framework.math.Vector3
import framework.math.internal.getTranslation
import framework.math.internal.setTranslation
import web.gl.WebGL2RenderingContext

interface Node {
    val name: String?
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
    val rotationMatrix: Matrix3
    var direction: Vector3
    fun lookAt(target: Vector3)
    fun findByName(name: String): Node?
}

class NodeImpl(override val name: String? = null) : Node {

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

    override val rotationMatrix: Matrix3
        get() = Matrix3.fromMatrix4(transform)

    override var direction: Vector3
        get() = rotationMatrix * Vector3(0f, 0f, -1f)
        set(value) {
            val target = position + value
            lookAt(target)
        }

    override fun lookAt(target: Vector3) {
        Matrix4.lookAt(transform, worldPosition, target, Vector3(0f, 1f, 0f))
    }

    override fun findByName(name: String): Node? {
        if (this.name == name) {
            return this
        }
        for (child in children) {
            val node = child.findByName(name)
            if (node != null) {
                return node
            }
        }
        return null
    }
}

typealias Scene = NodeImpl

typealias Group = NodeImpl

enum class TransformType {
    Local,
    Global
}

class NodeBuilder(private val gl: WebGL2RenderingContext, private val built: Node) {

    fun add(node: Node) {
        built.add(node)
    }

    fun mesh(
        geometry: Supplier<Geometry>,
        material: Supplier<Material>,
        mode: Mode = Mode.Triangles,
        name: String? = null,
        block: ((NodeBuilder).() -> Unit)? = null
    ) {
        val mesh = Mesh(gl, geometry(gl), material(gl), mode, name)
        built.add(mesh)
        if (block != null) {
            NodeBuilder(gl, mesh).apply(block)
        }
    }

    fun translate(x: Float, y: Float, z: Float, type: TransformType = Local) {
        built.translate(x, y, z, type)
    }

    fun scale(s: Float, type: TransformType = Local) {
        built.scale(s, type)
    }

    fun build(): Node = built
}

fun scene(gl: WebGL2RenderingContext, block: (NodeBuilder).() -> Unit): Node =
    NodeBuilder(gl, Group()).apply(block).build()
