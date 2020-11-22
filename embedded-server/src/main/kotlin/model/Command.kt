package model

import kotlinx.coroutines.channels.Channel
import sendToController

class CommandRequest(val commandSpeed: CommandSpeed)

data class CommandSpeed(val port: String, val speed: Int)
data class CommandState(val port: String, val state: String)

object CommandManagement {
    lateinit var commandChannel: Channel<CommandRequest>

    fun createChannel() {
        commandChannel = Channel()
    }
}

//FIXME: порт не так проверять --- не пофиксить, пока нет списка (поменять else branch)
fun validatePort(port: String?): String? {
    return when (port) {
        null -> "Null port isn't allow"
        else -> if (port in "com1".."com4") null else "Illegal port name, use comX, X - number "
    }
}

fun validateSpeed(speed: Int): String? {
    return if (speed in 4800..11520) null else "Unknown speed"
}

fun validateState(state: String): String? {
    return if (state.contains("on") || state.contains("off")) null else "Illegal state value"
}

fun validateParamsForSpeed(port: String?, speed: Int): String? {
    return when (val r = validatePort(port)) {
        null -> validateSpeed(speed)
        else -> r
    }
}

fun validateParamsForState(port: String?, state: String): String? {
    return when (val r = validatePort(port)) {
        null -> validateState(state)
        else -> r
    }
}

suspend fun processCommandQueries(chanel: Channel<CommandRequest> = CommandManagement.commandChannel) {
    for (element in chanel) {
        println("next command: $element")
        val string = "${element.commandSpeed.port}&${element.commandSpeed.speed}"
        sendToController(string)
    }
}