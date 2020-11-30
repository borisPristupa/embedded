import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.utils.io.errors.*
import itmo.embedded.http.runServer
import itmo.embedded.tcp.startTcpServer
import kotlinx.coroutines.*
import model.*
import java.net.InetAddress
import java.net.UnknownHostException

const val DEFAULT_TCP_HOST = "localhost"

fun isValidHost(host: String): Boolean = try {
    InetAddress.getByName(host)
    true
} catch (e: UnknownHostException) {
    false
}

lateinit var sendToController: suspend (String) -> Boolean

@KtorExperimentalAPI
suspend fun main(args: Array<String>): Unit = coroutineScope {
    val host = if (args.isNotEmpty() && isValidHost(args[0])) args[0] else DEFAULT_TCP_HOST

    UpdateStorage.createChannel()
    CommandManagement.createChannel()
    launch {
        processCommandQueries()
    }
    launch {
        processUpdateQueries()
    }
    launch {
        startTcpServer(host, 80) {
            sendToController = { msg: String ->
                var res = true
                try {
                    write(msg)
                } catch (e: IOException) {
                    res = false
                }
                res
            }
            while (true) {
                val input = try {
                    readLine()
                } catch (io: IOException) {
                    break
                }
                convertNmeaToJson(input)?.let { UpdateStorage.channel.send(it) }
            }
        }
    }
    embeddedServer(Netty, 8080, host) { runServer() }.start(wait = true)
}