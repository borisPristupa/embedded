package itmo.embedded.http

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.gson.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import model.*

fun Application.runServer() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter())
    }
    install(CORS) {
        anyHost()
    }
    routing {
        get("/gps") {
            println("new request")
            val channel = UpdateStorage.channel
            val result = CompletableDeferred<Update?>()
            launch {
                /* just for test */
//                UpdateStorage.channel.send(WriteQuery(Update("test data")))
                channel.send(ReadQuery(result))
            }
            result.await()?.let { r -> call.respond(r) } ?: call.respond(HttpStatusCode.NotFound, "no actual data")
        };
        get("/manage") {
            try {
                val speed = call.parameters["speed"]!!.toInt()
                val port = call.parameters["port"]!!.toInt()
                when (val r = validateParams(speed, port)) {
                    null -> {
                        val newCommand = Command(speed, port)
                        // todo() send to tcp
                        println("New command: port #$port to speed $speed")
                        call.respond(HttpStatusCode.OK)
                    }
                    else -> {
                        /* 401 - validation error */
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                println("Illegal url params")
                /* 404 - not enough url params   */
                call.respond(HttpStatusCode.NotFound, "Request must include port and speed")
            }
        }
    }
}
