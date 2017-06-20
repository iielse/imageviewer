package ch.ielse.demo.p05


object BusEvent {
    val FINISH = 1
    val FINISH_EXCEPT = 2
    val FINISH_ALL = 3


    var act: Int = 0
    var obj: Any? = null
    var obj2: Any? = null

    override fun toString(): String {
        return "BusEvent{" +
                "act=" + act +
                ", obj=" + obj +
                ", obj2=" + obj2 +
                '}'
    }
}
