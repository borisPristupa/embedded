package model

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import sendToController

class CommandRequest(val commandSpeed: CommandSpeed, val result: CompletableDeferred<String?>)

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
        else -> if (port in "com1".."com4") null else "Illegal port name: $port. Use comX, X from 1 to 4 "
    }
}

fun validateSpeed(speed: Int): String? {
    return if (speed in 4800..115200) null else "Illegal speed value: $speed. Use [4800; 115200]"
}

fun validateState(state: String): String? {
    return if (state.contains("on") || state.contains("off")) null else "Illegal state value: $state. Use on/off "
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

// todo() write to log file
suspend fun processCommandQueries(chanel: Channel<CommandRequest> = CommandManagement.commandChannel) {
    for (element in chanel) {
        println("next command: $element")
        val string = "${element.commandSpeed.port}&${element.commandSpeed.speed}"
        try {
            sendToController(string)
            println("manage(): $string")
            element.result.complete(null)
        } catch (e: UninitializedPropertyAccessException) {
            println("manage(): no tcp connection found")
            element.result.complete("No tcp connection found. Speed hasn't been changed")
        }

    }
}