package model

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

/**
 * От stm раз в секунду (+/-) приходит сообщение с новыми данными
 * Это сообщение содержит № порта и информацию от этого порта
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

open class Query
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

    fun getUpdate(portNumber: String): Update? {
        return dataMap[portNumber]
    }
}

fun convertNmeaToJson(nmeaText: String): UpdateWriteQuery? {
    try {
        val port = nmeaText.substring(1, nmeaText.indexOf("$"))
        if (validatePort(port).isNullOrEmpty()) {
            println("new update from port $port")
            val nmeaTextVars = nmeaText.substring(nmeaText.indexOf(",") + 1)
            val varsArray = nmeaTextVars.split(",")
            val update = Update(
                varsArray[0],
                varsArray[1],
                varsArray[2],
                varsArray[3],
                varsArray[4],
                varsArray[5],
                varsArray[6],
                varsArray[7],
                varsArray[8],
                varsArray[9],
                varsArray[10].substring(0, 1),
                varsArray[10].substring(1)
            )
            return UpdateWriteQuery(update, port)
        } else {
            // todo() log.error()
            println("parse: illegal port")
            return null
        }
    } catch (e: StringIndexOutOfBoundsException) {
        // todo() log.error
        println("parse failed - illegal vars number")
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
        when (element) {
            is UpdateReadQuery -> {
                println("NEW READ REQUEST: " + UpdateStorage.getUpdate(element.portNumber))
                element.result.complete(UpdateStorage.getUpdate(element.portNumber))
            }
            is UpdateWriteQuery -> {
                UpdateStorage.update(element.portNumber, element.update)
                println("NEW UPDATE REQUEST: " + UpdateStorage.getUpdate(element.portNumber))
            }
        }
    }
}


//   <com1$GPGSV,3,1,11,10,63,137,17,07,61,098,15,05,59,290,20,08,54,157,30*70
//   <com1$GPGSV,2,1,07,04,62,120,47,09,52,292,53,07,42,044,41,24,38,179,45*7B