import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.utils.io.errors.*
import itmo.embedded.http.runServer
import itmo.embedded.tcp.startTcpServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.Update
import model.UpdateStorage
import model.WriteQuery
import model.processQueries

@KtorExperimentalAPI
fun main(): Unit = runBlocking {
    UpdateStorage.createChannel()
    GlobalScope.async {
        processQueries()
    }
    GlobalScope.launch {
        startTcpServer("127.0.0.1", 80) {
            while (true) {
                val input = try {
                    readLine()
                } catch (io: IOException) {
                    break
                }
                UpdateStorage.channel.send(WriteQuery(Update(input)))
            }
        }
    }
    embeddedServer(Netty, 8080) { runServer() }.start(wait = true)
}