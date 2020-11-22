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
 *  REQUESTS:
 * /gps - old version for version compatibility - no params
 * /data - new version. Port::String (now - "comX", where X - number from 1 to 4, port name maybe will change)
 *
 * /manage - change speed for specified port. Port::String, Speed::Int
 * /change_port_state - change port state. Port::String, State::Boolean - True:on, False:Off
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
                channel.send(UpdateReadQuery(result, "com1"))
            }
            result.await()?.let { r -> call.respond(r) } ?: call.respond(HttpStatusCode.NotFound, "no actual data")
        }
        get("/data") {
            try {
                val port = call.parameters["port"]!!
                when (val r = validatePort(port)) {
                    null -> {
                        val gpsChannel = UpdateStorage.channel
                        val result = CompletableDeferred<Update?>()
                        launch {
                            gpsChannel.send(UpdateReadQuery(result, port))
                        }
                        val TooEarly = HttpStatusCode(425, "Too early");
                        result.await()?.let { res -> call.respond(res) } ?: call.respond(
                            TooEarly, "no actual data"
                        )
                    }
                    else -> {
                        log.error("401 - validation error")
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                log.error("data(): Illegal url params")
                log.error("404 - not enough url params")
                call.respond(HttpStatusCode.NotFound, "Request must include port")
            } catch (e: IllegalArgumentException) {
                log.error("data(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Must be numbers")
            }
        }
        get("/manage") {
            try {
                val port = call.parameters["port"]!!
                val speed = call.parameters["speed"]!!.toInt()
                when (val r = validateParamsForSpeed(port, speed)) {
                    null -> {
                        val commandChannel = CommandManagement.commandChannel
                        commandChannel.send(CommandRequest(CommandSpeed(port, speed)))
                        log.debug("New command: port #$port to speed $speed")
                        call.respond(HttpStatusCode.OK)
                    }
                    else -> {
                        log.error("401 - validation error")
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                log.error("404 - not enough url params - port and speed")
                call.respond(HttpStatusCode.NotFound, "Request must include port and speed")
            } catch (e: IllegalArgumentException) {
                log.error("manage(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Post is string, speed is number")
            }
        }
        get("/change_port_state") {
            try {
                val port = call.parameters["port"]!!
                val state = call.parameters["state"]!!.toString()
                when (val r = validateParamsForState(port, state)) {
                    null -> {
//                        val newCommand = CommandState(port, state)
                        // todo() if we decide to on/off port -> create another request, and add this request processing (-> processCommandQueries )
                        /* val commandChannel = CommandManagement.commandChannel
                        commandChannel.send(CommandRequest(port, state)) */
                        if (state.contains("on")) {
                            log.debug("New command: port #$port to state On")
                        } else {
                            log.debug("New command: port #$port to state Off")
                        }
                        call.respond(HttpStatusCode.OK, "State switched!")
                    }
                    else -> {
                        log.error("401 - validation error")
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                log.error("change_port_state(): Illegal url params")
                log.error("404 - not enough url params - port and state")
                call.respond(HttpStatusCode.NotFound, "Request must include port and state")
            } catch (e: IllegalArgumentException) {
                log.error("change_port_state(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Port must be number")
            }
        }
    }
}
