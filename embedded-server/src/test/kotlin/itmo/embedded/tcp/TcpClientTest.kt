package itmo.embedded.tcp

import io.ktor.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.time.Duration

private const val HOST = "127.0.0.1"
private const val PORT = 801

@KtorExperimentalAPI
class TcpClientTest {
    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            GlobalScope.launch {
                startTcpServer(HOST, PORT) {
                    while (true) {
                        try {
                            write("${readLine()}\n")
                        } catch (io: IOException) {
                            break
                        }
                    }
                }
            }
            runBlocking {
                delay(1000) // let the server start
            }
        }
    }

    @BeforeEach
    fun setUp() {
        val socket = Socket(HOST, PORT)
        input = socket.getInputStream().bufferedReader()
        output = PrintWriter(socket.getOutputStream())
    }

    @AfterEach
    fun tearDown() {
        input.close()
        output.close()
    }

    @Test
    fun testTcpClient() {
        TcpClientTest::class.java
            .getResourceAsStream("/nmeaExample.txt")
            .bufferedReader()
            .lines()
            .forEach {
                output.println(it)
                output.flush()
                val answer = assertTimeout(Duration.ofSeconds(2)) {
                    input.readLine()
                }
                assertEquals(it, answer)
            }
    }
}
