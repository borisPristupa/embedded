import io.ktor.server.engine.*
import io.ktor.server.netty.*
import itmo.embedded.http.runServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import model.UpdateStorage
import model.processQueries

fun main(): Unit = runBlocking {
    UpdateStorage.createChannel()
    GlobalScope.async {
        processQueries()
    }
    embeddedServer(Netty, 8080) { runServer() }.start(wait = true)
}