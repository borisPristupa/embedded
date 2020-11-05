import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import model.Query
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.streams.toList

private fun loadStubData(): Sequence<String>? =
    Query::class.java
        .getResourceAsStream("/nmeaStub.txt")
        ?.bufferedReader()
        ?.lines()
        ?.toList()
        ?.let {
            sequence {
                var i = 0
                while (true) {
                    yield(it[i++])
                    i %= it.size
                }
            }
        }

fun main(args: Array<String>) {
    val tcpHost = if (args.isNotEmpty() && isValidHost(args[0])) args[0] else DEFAULT_TCP_HOST

    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        println("Goodbye...")
    })

    val data = loadStubData() ?: generateSequence {
        "This is the default stub, add 'nmeaStub.txt' resource for stub definition"
    }
    PrintWriter(Socket(tcpHost, 80).getOutputStream()).use { out ->
        println("Connected to the server")
        data.forEach {
            out.println(it)
            out.flush()
            runBlocking { delay(1000) }
        }
    }
}