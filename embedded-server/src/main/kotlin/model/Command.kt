package model

data class CommandSpeed(val speed: Int, val port: Int)
data class CommandState(val port: Int, val state: Int)


fun validateParamsForSpeed(speed: Int, port: Int): String? {
    return if (speed > 11520 || speed < 4800) "Illegal speed value"
    else if (port < 0) "Illegal port value"
    else null
}

fun validateParamsForState(port: Int, state: Int): String? {
    return if (port < 0) "Illegal port value"
    else if (!(port == 1  || port == 0)) "Illegal state value"
    else null
}
