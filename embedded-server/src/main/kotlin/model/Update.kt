package model

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

data class Update(val rowData: String?)

open class Query();
class ReadQuery(val result: CompletableDeferred<Update?>) : Query()
class WriteQuery(val update: Update) : Query()

object UpdateStorage {
    var lastUpdate: Update? = null;
    lateinit var channel: Channel<Query>

    fun createChannel() {
        channel = Channel()
    }

    fun update(newUpdate: Update) {
        lastUpdate = newUpdate
    }

    fun getUpdate(): Update? {
        return lastUpdate
    }
}

suspend fun processQueries(chanel: Channel<Query> = UpdateStorage.channel) {
    for (element in chanel) {
        println(element)
        when (element) {
            is ReadQuery -> {
                println(UpdateStorage.getUpdate())
                element.result.complete(UpdateStorage.getUpdate())
            }
            is WriteQuery -> {
                UpdateStorage.update(element.update)
                println(UpdateStorage.getUpdate())
            }
        }
    }
}