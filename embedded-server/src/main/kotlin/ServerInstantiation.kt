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

/**
 * this host is used both for tcp and http connections;
 * it's possible to set custom host: add a program argument like 192.168.1.107
 */
const val DEFAULT_TCP_HOST = "localhost"

fun isValidHost(host: String): Boolean = try {
    InetAddress.getByName(host)
    true
} catch (e: UnknownHostException) {
    false
}

lateinit var sendToController: suspend (String) -> Boolean

/**
 * Отсюда начинается сервер...
 */
@KtorExperimentalAPI
suspend fun main(args: Array<String>): Unit = coroutineScope {
    val host = if (args.isNotEmpty() && isValidHost(args[0])) args[0] else DEFAULT_TCP_HOST

    /**
     * Init two coroutines channels and their handlers
     * See details in Update.kt & Command.kt
     */
    UpdateStorage.createChannel()
    CommandManagement.createChannel()
    launch {
        processCommandQueries()
    }
    launch {
        processUpdateQueries()
    }

    // start tcp server (describe host & port, and main methods: write, read). IMPORTANT - use 80 port for tcp
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

    // start http server. IMPORTANT - use 8080 port for http
    embeddedServer(Netty, 8080, host) { runServer() }.start(wait = true)
}