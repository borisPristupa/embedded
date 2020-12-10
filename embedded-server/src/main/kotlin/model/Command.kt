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

// ВАЛИДАЦИЯ порта, скорости, состояния
fun validatePort(port: String?): String? {
    return when (port) {
        null -> "Null port isn't allow"
        else -> if (port in "com1".."com9") null else "Illegal port name: $port. Use comX, X from 1 to 9 "
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

/**
 * Снова корутины :)
 * логика аналогична той, что используется в Update.kt с тем лишь отчличием, что всего один тип запроса: CommandRequest
 *
 * Цель: предотвращение одновременного запроса на изменение скорости одного и того же порта
 *
 * Логика: от http-клиента приходит запрос, встает в очередь на обработку.
 *      Обработчик считывает запрос, пытается отправить контроллеру (если не успешно -> пользователь об этом узнает).
 */
suspend fun processCommandQueries(chanel: Channel<CommandRequest> = CommandManagement.commandChannel) {
    for (element in chanel) {
        println("next command: $element")
        val string = "${element.commandSpeed.port}&${element.commandSpeed.speed}"
        try {
            if (sendToController(string)) {
                println("manage(): $string")
                element.result.complete(null)
            } else {
                println("manage(): tcp connection is closed")
                element.result.complete("Tcp connection is closed. Speed hasn't been changed")
            }
        } catch (e: UninitializedPropertyAccessException) {
            println("manage(): no tcp connection found")
            element.result.complete("No tcp connection found. Speed hasn't been changed")
        }
    }
}