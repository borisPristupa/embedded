package itmo.embedded.http

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.gson.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import model.ReadQuery
import model.Update
import model.UpdateStorage

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
        }
    }
}