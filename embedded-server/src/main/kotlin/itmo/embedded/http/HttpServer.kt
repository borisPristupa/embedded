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

/**
 * /gps -
 * /manage - change speed for specified port. Port::Int, Speed::Int
 * /change_port_state - change port state. Port::Int, State::Boolean - True:on, False:Off
 */
fun Application.runServer() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter())
    }
    install(CORS) {
        anyHost()
    }
    routing {
        get("/gps") {
            val channel = UpdateStorage.channel
            val result = CompletableDeferred<Update?>()
            launch {
                channel.send(UpdateReadQuery(result, 1))
            }
            result.await()?.let { r -> call.respond(r) } ?: call.respond(HttpStatusCode.NotFound, "no actual data")
        }
        get("/data") {
            try {
                val port = call.parameters["port"]!!.toInt()
                when (val r = validatePort(port)) {
                    null -> {
                        val gpsChannel = UpdateStorage.channel
                        val result = CompletableDeferred<Update?>()
                        launch {
                            gpsChannel.send(UpdateReadQuery(result, port))
                        }
                        result.await()?.let { res -> call.respond(res) } ?: call.respond(
                            HttpStatusCode.NotFound,
                            "no actual data"
                        )
                    }
                    else -> {
                        /* 401 - validation error */
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                println("data(): Illegal url params")
                /* 404 - not enough url params   */
                call.respond(HttpStatusCode.NotFound, "Request must include port")
            } catch (e: IllegalArgumentException) {
                println("data(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Must be numbers")
            }
        }
        get("/manage") {
            try {
                val speed = call.parameters["speed"]!!.toInt()
                val port = call.parameters["port"]!!.toInt()
                when (val r = validateParamsForSpeed(speed, port)) {
                    null -> {
                        val commandChannel = CommandManagement.commandChannel
                        commandChannel.send(CommandRequest(CommandSpeed(speed, port)))
                        println("New command: port #$port to speed $speed")
                        call.respond(HttpStatusCode.OK)
                    }
                    else -> {
                        /* 401 - validation error */
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                println("manage(): Illegal url params")
                /* 404 - not enough url params   */
                call.respond(HttpStatusCode.NotFound, "Request must include port and speed")
            } catch (e: IllegalArgumentException) {
                println("manage(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Must be numbers")
            }
        }
        get("/change_port_state") {
            try {
                val port = call.parameters["port"]!!.toInt()
                val state = call.parameters["state"]!!.toInt()
                when (val r = validateParamsForState(port, state)) {
                    null -> {
                        val newCommand = CommandState(port, state)
                        // todo() send to tcp
                        if (state == 1) {
                            println("New command: port #$port to state On")
                        } else {
                            println("New command: port #$port to state Off")
                        }
                        call.respond(HttpStatusCode.OK, "State switched!")
                    }
                    else -> {
                        /* 401 - validation error */
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                println("change_port_state(): Illegal url params")
                /* 404 - not enough url params   */
                call.respond(HttpStatusCode.NotFound, "Request must include port and state")
            } catch (e: IllegalArgumentException) {
                println("change_port_state(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Port and state must be numbers")
            }
        }
    }
}
