package itmo.embedded.tcp

import io.ktor.util.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
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
        @BeforeClass
        fun beforeClass() {
            GlobalScope.launch {
                startTcpServer(HOST, PORT) {
                    while (true) {
                        write("${readLine()}\n")
                    }
                }
            }
        }
    }

    @Before
    fun setUp() {
        val socket = Socket(HOST, PORT)
        input = socket.getInputStream().bufferedReader()
        output = PrintWriter(socket.getOutputStream())
    }

    @After
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
