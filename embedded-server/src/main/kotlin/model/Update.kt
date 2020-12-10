package model

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

/**
 * От stm раз в секунду (+/-) приходит сообщение с новыми данными
 * Это сообщение содержит № порта и информацию от этого порта
 * --- данные от разных портов похожи по структуре или нет....
 * Это сообщение нужно распарсить, и сохранить в map dataMap - ключ = № порта
 * Порты: com1 - com9, используется до 8, 9 на всякий случай :)
 */
// basic class for parsed data, below is gprmc & hehdt child-classes
open class Update()

data class Update_gprmc(
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
) : Update()

data class Update_hehdt(
    val coordinate: String?,
    val true_word: String?,
    val checksum: String?
) : Update()

//basic class for queries: it's used for channels and two child-classes
open class Query
class UpdateReadQuery(val result: CompletableDeferred<Update?>, val portNumber: String) : Query()
class UpdateWriteQuery(val update: Update, val portNumber: String) : Query()

/**
 * Небольшая серверная логика
 * - сохранение новых данных,
 * - получение данных для порта.
 */
object UpdateStorage {
    lateinit var channel: Channel<Query>
    private val dataMap = mutableMapOf<String, Update?>()

    fun createChannel() {
        channel = Channel()
    }

    fun update(portNumber: String, newUpdate: Update) {
        dataMap[portNumber] = newUpdate
    }

    fun getUpdate(portNumber: String): Update? {
        return dataMap[portNumber]
    }
}

// ПАРСЕР
fun convertNmeaToJson(nmeaText: String): UpdateWriteQuery? {
    try {
        val port = nmeaText.substring(1, nmeaText.indexOf("$"))
        val type = nmeaText.substring(nmeaText.indexOf("$") + 1, nmeaText.indexOf("$") + 6)
        if (validatePort(port).isNullOrEmpty()) {
            println("new update from port $port")
            val nmeaTextVars = nmeaText.substring(nmeaText.indexOf(",") + 1)
            val varsArray = nmeaTextVars.split(",")
            var update = Update()
            if (type.toLowerCase().equals("gprmc")) {
                update = Update_gprmc(
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
            } else {
                update = Update_hehdt(
                    varsArray[0],
                    varsArray[1].substring(0, 1),
                    varsArray[1].substring(1)
                )
            }
            return UpdateWriteQuery(update, port)
        } else {
            // todo() log.error()
            println("parse: illegal port")
            return null
        }
    } catch (e: StringIndexOutOfBoundsException) {
        // todo() log.error()
        println("parse failed - illegal vars number")
        return null
    }
}

/*fun main(args: Array<String>) {
    var query = convertNmeaToJson("<com1\$GPRMC,081836,A,3751.65,S,14507.36,E,000.0,360.0,130998,011.3,E*62")
    print(Gson().toJson(query))
    query = convertNmeaToJson("<com1\$HEHDT,192.0,T*25")
    print(Gson().toJson(query))
}*/

/**
 * !!! КОРУТИНЫ ~ легковесные потоки
 * 1) Когда от контроллера приходит сообщение, создается запрос на его обработку - UpdateReadQuery,
 *      далее этот запрос помещается в очередь (ServerInstantiation line 65)
 *      P.S. каналы гарантируют fifo и последовательную обработку
 *    При получении такого запроса обработчик записывает/перезаписывает данные для данного порта
 *      здесь используется map, ключ - имя порта
 * 2) Когда от http-клиента приходит запрос, создается UpdateWriteRequest
 *      Получая такое сообщение, обработчик возвращает информацию для нужного порта.
 */
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

/**
 * test data!!!
 * <com1$GPGSV,3,1,11,10,63,137,17,07,61,098,15,05,59,290,20,08,54,157,30*70
 * <com1$GPGSV,2,1,07,04,62,120,47,09,52,292,53,07,42,044,41,24,38,179,45*7B
 */
