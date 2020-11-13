package model

import kotlinx.coroutines.channels.Channel

class CommandRequest(val commandSpeed: CommandSpeed)

data class CommandSpeed(val speed: Int, val port: Int)
data class CommandState(val port: Int, val state: String)

object CommandManagement {
    lateinit var commandChannel: Channel<CommandRequest>

    fun createChannel() {
        commandChannel = Channel()
    }
}

fun validatePort(port: Int): String? {
    return if (port in 1..3) null else "Unknown port"
}

fun validateParamsForSpeed(speed: Int, port: Int): String? {
    return if (speed > 11520 || speed < 4800) "Illegal speed value"
    else if (port < 0) "Illegal port value"
    else null
}

fun validateParamsForState(port: Int, state: String): String? {
    return if (port < 0) "Illegal port value"
    else if (!(state.contains("on") || state.contains("off"))) "Illegal state value"
    else null
}

suspend fun processCommandQueries(chanel: Channel<CommandRequest> = CommandManagement.commandChannel) {
    for (element in chanel) {
        println("next command: $element")
        val string = "${element.commandSpeed.port}&${element.commandSpeed.speed}"
        // todo() send command to stm
    }
}