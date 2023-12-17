package dat.gui

@JsModule("dat.gui")
@JsNonModule
external class dat {
    class GUI(params: dynamic = definedExternally) {
        fun addFolder(name: String): GUI
        fun add(target: dynamic, property: String): Controller<Number>
        fun add(
            target: dynamic,
            property: String,
            min: Number = definedExternally,
            max: Number = definedExternally,
            step: Number = definedExternally
        ): Controller<Number>

        fun addColor(target: dynamic, property: String): Controller<Array<Number>>
    }

    class Controller<V> {
        fun onChange(fnc: (V) -> Unit)
    }
}


