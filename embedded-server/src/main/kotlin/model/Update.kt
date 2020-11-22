package model

import io.ktor.application.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import java.util.logging.Logger
import kotlin.math.log

/**
 * От stm раз в секунду (+/-) приходит сообщение с новыми данными
 * Это сообщение содержит № порта и информацию от этого порта(????)
 * --- данные от разных портов похожи по структуре или нет....
 * full-duplex?????
 * Это сообщение нужно распарсить, и сохранить в map dataMap - ключ = № порта
 *
 */

data class Update(
    val time: String?,
    val validity: String?,
    val current_latitude: String?,
    val north_or_south: String?,
    val current_longitude: String?,
    val east_or_west: String?,
    val speed_in_knots: String?,
    val true_course: String?,
    val ut_date: String?,
    val variation: String?,
    val east_or_west_2: String?,
    val checksum: String?
)

open class Query()
class UpdateReadQuery(val result: CompletableDeferred<Update?>, val portNumber: String) : Query()
class UpdateWriteQuery(val update: Update, val portNumber: String) : Query()


object UpdateStorage {
    private var lastUpdate: Update? = null
    lateinit var channel: Channel<Query>
    private val dataMap = mutableMapOf<String, Update?>()

    fun createChannel() {
        channel = Channel()
    }

    fun update(portNumber: String, newUpdate: Update) {
        dataMap[portNumber] = newUpdate
        lastUpdate = newUpdate
    }

    fun getUpdate(potNumber: String): Update? {
        return dataMap[potNumber]
    }
}

fun convertNmeaToJson(nmeaText: String): UpdateWriteQuery? {
   try {
        val port = nmeaText.substring(1, nmeaText.indexOf("$"))
        println("new update from port $port")
        val nmeaTextVars = nmeaText.substring(nmeaText.indexOf(",") + 1)
        val vars_array = nmeaTextVars.split(",")
        val update = Update(
            vars_array[0],
            vars_array[1],
            vars_array[2],
            vars_array[3],
            vars_array[4],
            vars_array[5],
            vars_array[6],
            vars_array[7],
            vars_array[8],
            vars_array[9],
            vars_array[10].substring(0, 1),
            vars_array[10].substring(1)
        )
        return UpdateWriteQuery(update, port)
    } catch (e: StringIndexOutOfBoundsException) {
        print("parse() - failed")
        return null
    }
}

/*fun main(args: Array<String>) {
    val query = convertNmeaToJson("<com1\$GPRMC,081836,A,3751.65,S,14507.36,E,000.0,360.0,130998,011.3,E*62")
    print(Gson().toJson(query))
}*/

// todo() write to log file
suspend fun processUpdateQueries(chanel: Channel<Query> = UpdateStorage.channel) {
    for (element in chanel) {
        println(element)
        when (element) {
            is UpdateReadQuery -> {
                println("new read() "+UpdateStorage.getUpdate(element.portNumber))
                element.result.complete(UpdateStorage.getUpdate(element.portNumber))
            }
            is UpdateWriteQuery -> {
                UpdateStorage.update(element.portNumber, element.update)
                println("new update() "+UpdateStorage.getUpdate(element.portNumber))
            }
        }
    }
}