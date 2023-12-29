package framework.core

import web.dom.GlobalEventHandlers
import web.uievents.KeyboardEvent

interface KeyState {
    fun isPressed(key: String): Boolean
    fun isDown(key: String): Boolean
    fun isUp(key: String): Boolean
}

class Input : KeyState {

    private val down = mutableSetOf<String>()
    private val pressed = mutableSetOf<String>()
    private val up = mutableSetOf<String>()

    override fun isPressed(key: String): Boolean = key in pressed

    override fun isDown(key: String): Boolean = key in down

    override fun isUp(key: String): Boolean = key in up

    fun attachTo(handler: GlobalEventHandlers) {
        handler.onkeydown = this::onKeyDown
        handler.onkeyup = this::onKeyUp
    }

    fun reset() {
        down.clear()
        up.clear()
    }

    private fun onKeyDown(event: KeyboardEvent) {
        down.add(event.key)
        pressed.add(event.key)
    }

    private fun onKeyUp(event: KeyboardEvent) {
        up.add(event.key)
        pressed.remove(event.key)
    }
}
