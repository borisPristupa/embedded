package itmo.embedded.tcp

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ConnectionContext(private val socket: Socket) {
    private val inputChannel: ByteReadChannel = socket.openReadChannel()
    private val outputChannel: ByteWriteChannel = socket.openWriteChannel(true)

    val isClosed: Boolean
        get() = socket.isClosed

    /** Blocks until a new line character is met
     *
     * @throws IOException if the channel have been closed */
    suspend fun readLine(): String = inputChannel.readUTF8Line() ?: throw IOException("The channel is closed")

    @KtorExperimentalAPI
    suspend fun write(str: String) {
        outputChannel.write(str, Charsets.UTF_8)
    }
}

/** Starts a tcp server accepting multiple connections, then blocks */
@KtorExperimentalAPI
suspend fun startTcpServer(host: String, port: Int, onConnect: suspend ConnectionContext.() -> Unit) = coroutineScope {
    val serverSocket = aSocket(ActorSelectorManager(Dispatchers.IO))
        .tcp()
        .bind(host, port) {
            typeOfService = TypeOfService.IPTOS_LOWDELAY
        }

    while (true) {
        val socket = serverSocket.accept()
        launch {
            ConnectionContext(socket).onConnect()
        }
    }
}
