package framework.core

import web.uievents.KeyboardEvent

class KeyState {

    private val pressed: HashSet<String> = hashSetOf()

    fun setPressed(event: KeyboardEvent) {
        pressed.add(event.key)
    }

    fun setReleased(event: KeyboardEvent) {
        pressed.remove(event.key)
    }

    fun isPressed(key: String): Boolean = pressed.contains(key)
}
