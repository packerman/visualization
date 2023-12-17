package dat.gui

@JsModule("dat.gui")
@JsNonModule
external class dat {
    class GUI(params: dynamic = definedExternally) {
        fun addFolder(name: String): GUI
        fun add(target: dynamic, property: String): Controller
        fun add(
            target: dynamic,
            property: String,
            min: Number = definedExternally,
            max: Number = definedExternally,
            step: Number = definedExternally
        ): Controller

        fun addColor(target: dynamic, property: String): Controller
    }

    class Controller {
        fun <V> onChange(fnc: (V) -> Unit)
    }
}


