package model

data class Command(val speed: Int, val port: Int)


fun validateParams(speed: Int, port: Int): String? {
    return if (speed > 11520 || speed < 4800) "Illegal speed value"
    else if (port < 0) "Illegal port value"
    else null
}
