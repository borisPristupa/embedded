package itmo.embedded.tcp

import io.ktor.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

private const val HOST = "127.0.0.1"
private const val PORT = 80

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
                        write("${readLine()}\n")
                    }
                }
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
                assertEquals(it, input.readLine())
            }
    }
}
