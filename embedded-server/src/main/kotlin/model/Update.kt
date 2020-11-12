package model

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

/**
 * От stm раз в секунду (+/-) приходит сообщение с новыми данными
 * Это сообщение содержит № порта и информацию от этого порта(????)
 * --- данные от разных портов похожи по структуре или нет....
 * full-duplex?????
 * Это сообщение нужно распарсить, и сохранить в map dataMap - ключ = № порта
 *
 */

data class Update(val rowData: String?)

open class Query()
class UpdateReadQuery(val result: CompletableDeferred<Update?>, val portNumber: Int) : Query()
class UpdateWriteQuery(val update: Update, val portNumber: Int) : Query()


object UpdateStorage {
    private var lastUpdate: Update? = null
    lateinit var channel: Channel<Query>
    private val dataMap = mutableMapOf<Int, Update?>()

    fun createChannel() {
        channel = Channel()
    }

    fun update(portNumber: Int, newUpdate: Update) {
        dataMap[portNumber] = newUpdate
        lastUpdate = newUpdate
    }

    fun getUpdate(potNumber: Int): Update? {
        return dataMap[potNumber]
    }
}

suspend fun processUpdateQueries(chanel: Channel<Query> = UpdateStorage.channel) {
    for (element in chanel) {
        println(element)
        when (element) {
            is UpdateReadQuery -> {
                println(UpdateStorage.getUpdate(element.portNumber))
                element.result.complete(UpdateStorage.getUpdate(element.portNumber))
            }
            is UpdateWriteQuery -> {
                UpdateStorage.update(element.portNumber, element.update)
                println(UpdateStorage.getUpdate(element.portNumber))
            }
        }
    }
}