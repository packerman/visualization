package framework.core

import dat.gui.dat.GUI
import framework.math.Vector3
import framework.math.Vector4
import js.core.jso

fun gui(block: (GUIBuilder).() -> Unit): GUI = gui(GUI(), block)

fun gui(name: String, block: (GUIBuilder).() -> Unit): GUI {
    val params: dynamic = jso()
    params.name = name
    return gui(GUI(params), block)
}

fun gui(name: String, width: Number, block: (GUIBuilder).() -> Unit): GUI {
    val params: dynamic = jso()
    params.name = name
    params.width = width
    return gui(GUI(params), block)
}

private fun gui(gui: GUI, block: (GUIBuilder).() -> Unit): GUI {
    val builder = GUIBuilder(gui)
    block(builder)
    return gui
}

class GUIBuilder(private val gui: GUI) {

    fun color(name: String, value: Vector3, onChange: (Vector3) -> Unit) {
        val state: dynamic = jso()
        state[name] = arrayOf(value.x * 255f, value.y * 255f, value.z * 255f)
        val controller = gui.addColor(state, name)
        controller.onChange {
            val x = it[0].toFloat() / 255f
            val y = it[1].toFloat() / 255f
            val z = it[2].toFloat() / 255f
            onChange(Vector3(x, y, z))
        }
    }

    fun color(name: String, value: Vector4, onChange: (Vector4) -> Unit) {
        val state: dynamic = jso()
        state[name] = arrayOf(value.x * 255f, value.y * 255f, value.z * 255f, value.w * 255f)
        val controller = gui.addColor(state, name)
        controller.onChange {
            val x = it[0].toFloat() / 255f
            val y = it[1].toFloat() / 255f
            val z = it[2].toFloat() / 255f
            val w = it[3].toFloat() / 255f
            onChange(Vector4(x, y, z, w))
        }
    }

    fun number(
        name: String,
        value: Float,
        range: ClosedFloatingPointRange<Float>,
        step: Float,
        onChange: (Float) -> Unit
    ) {
        val state: dynamic = jso()
        state[name] = value
        val controller = gui.add(state, name, range.start, range.endInclusive, step)
        controller.onChange { onChange(it.toFloat()) }
    }

    fun number(
        name: String,
        value: Float,
        onChange: (Float) -> Unit
    ) {
        val state: dynamic = jso()
        state[name] = value
        val controller = gui.add(state, name)
        controller.onChange { onChange(it.toFloat()) }
    }

    fun vector(
        name: String,
        value: Vector3,
        range: ClosedFloatingPointRange<Float>,
        step: Float,
        onChange: (Vector3) -> Unit
    ) {
        val nameX = "$name X"
        val nameY = "$name Y"
        val nameZ = "$name Z"
        val state: dynamic = jso()
        val callback = { _: dynamic, _: Float ->
            onChange(
                Vector3(
                    state[nameX] as Float,
                    state[nameY] as Float,
                    state[nameZ] as Float,
                )
            )
        }
        add(state, nameX, value.x, range, step, callback)
        add(state, nameY, value.y, range, step, callback)
        add(state, nameZ, value.z, range, step, callback)
    }

    fun folder(name: String, block: GUIBuilder.() -> Unit) {
        gui(this.gui.addFolder(name), block)
    }

    private fun add(
        state: dynamic,
        name: String,
        value: Float,
        range: ClosedFloatingPointRange<Float>,
        step: Float,
        onChange: (dynamic, Float) -> Unit
    ) {
        state[name] = value
        val controller = gui.add(state, name, range.start, range.endInclusive, step)
        controller.onChange { onChange(state, it.toFloat()) }
    }
}

