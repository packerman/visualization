package common

import dat.gui.dat.GUI
import js.core.jso

fun gui(name: String, width: Number, block: (GUIBuilder).() -> Unit): GUI {
    val builder = GUIBuilder(name, width)
    block(builder)
    return builder.build()
}

class GUIBuilder(name: String, width: Number) {

    private val params: dynamic = jso()
    private val gui: GUI

    init {
        params.name = name
        params.width = width
        gui = GUI(params)
    }

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
            onChange(Vector3(
                state[nameX] as Float,
                state[nameY] as Float,
                state[nameZ] as Float,
            ))
        }
        add(state, nameX, value.x, range, step, callback)
        add(state, nameY, value.y, range, step, callback)
        add(state, nameZ, value.z, range, step, callback)
    }

    private fun add(state: dynamic,
                    name: String,
                    value: Float,
                    range: ClosedFloatingPointRange<Float>,
                    step: Float,
                    onChange: (dynamic, Float) -> Unit) {
        state[name] = value
        val controller = gui.add(state, name, range.start, range.endInclusive, step)
        controller.onChange { onChange(state, it.toFloat()) }
    }

    fun build(): GUI = gui
}

