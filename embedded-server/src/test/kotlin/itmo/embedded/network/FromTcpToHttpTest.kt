package itmo.embedded.network

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import itmo.embedded.http.runServer
import itmo.embedded.tcp.startTcpServer
import kotlinx.coroutines.*
import model.Update
import model.UpdateStorage
import model.WriteQuery
import model.processQueries
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import kotlin.test.assertEquals

private const val HOST = "127.0.0.1"
private const val TCP_PORT = 801
private const val HTTP_PORT = 8080

@KtorExperimentalAPI
class FromTcpToHttpTest {
    private lateinit var output: PrintWriter
    private lateinit var client: HttpClient

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            UpdateStorage.createChannel()
            GlobalScope.launch {
                processQueries()
            }
            GlobalScope.launch {
                startTcpServer(HOST, TCP_PORT) {
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
            GlobalScope.launch {
                embeddedServer(Netty, HTTP_PORT, host = HOST) { runServer() }.start(wait = true)
            }
            runBlocking {
                delay(1000) // just for the servers to start
            }
        }
    }

    @BeforeEach
    fun setUp() {
        val socket = Socket(HOST, TCP_PORT)
        output = PrintWriter(socket.getOutputStream())

        client = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
    }

    @AfterEach
    fun tearDown() {
        output.close()
    }

    @Test
    fun testUpdatesWithDelay() {
        FromTcpToHttpTest::class.java
            .getResourceAsStream("/nmeaExample.txt")
            .bufferedReader()
            .lines()
            .forEach {
                runBlocking {
                    output.println(it)
                    output.flush()
                    val update = client.get<Update>("gps") {
                        port = HTTP_PORT
                    }
                    assertEquals(it, update.rowData)
                }
            }

    }
}