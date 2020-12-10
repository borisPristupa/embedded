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
 * /data - port::String (now - "comX", where X - number from 1 to 4, port name maybe will change) - get update by port
 *
 * /manage - change speed for specified port. Port::String, Speed::Int
 * /change_port_state - change port state. Port::String, State::Boolean - True:on, False:Off
 */

val TooEarly = HttpStatusCode(425, "Too early")

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
            channel.send(UpdateReadQuery(result, "com1"))
            result.await()?.let { r -> call.respond(r) } ?: call.respond(TooEarly, "no actual data")
        }
        get("/data") {
            try {
                val port = call.parameters["port"]!!
                when (val r = validatePort(port)) {
                    null -> {
                        val gpsChannel = UpdateStorage.channel
                        val result = CompletableDeferred<Update?>()
                        gpsChannel.send(UpdateReadQuery(result, port))
                        result.await()?.let { res -> call.respond(res) } ?: call.respond(
                            TooEarly, "no actual data"
                        )
                    }
                    else -> {
                        log.error("data(): 400 - validation error $r")
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                log.error("data(): Illegal url params")
                log.error("data(): 404 - not enough url params")
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
                        val result = CompletableDeferred<String?>()
                        commandChannel.send(CommandRequest(CommandSpeed(port, speed), result))
                        result.await()?.let { res -> call.respond(HttpStatusCode.InternalServerError, res) }
                            ?: call.respond(HttpStatusCode.OK)
                    }
                    else -> {
                        log.error("manage(): 400 - validation error $r")
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                log.error("manage(): 404 - not enough url params - port and speed")
                call.respond(HttpStatusCode.NotFound, "Request must include port and speed")
            } catch (e: IllegalArgumentException) {
                log.error("manage(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Post is string, speed is number")
            }
        }
//            Возможное расщирение - включение/выключение порта.
        get("/change_port_state") {
            try {
                val port = call.parameters["port"]!!
                val state = call.parameters["state"]!!.toString()
                when (val r = validateParamsForState(port, state)) {
                    null -> {
                        //val newCommand = CommandState(port, state)
                        // todo() if we decide to on/off port -> create another request, and add this request processing (-> processCommandQueries )
                        /* val commandChannel = CommandManagement.commandChannel
                        commandChannel.send(CommandRequest(port, state)) */
                        if (state.contains("on")) {
                            log.debug("change_port_state(): New command: port #$port to state On")
                        } else {
                            log.debug("change_port_state(): New command: port #$port to state Off")
                        }
                        call.respond(HttpStatusCode.OK, "State switched!")
                    }
                    else -> {
                        log.error("change_port_state(): 400 - validation error $r")
                        call.respond(HttpStatusCode.BadRequest, r)
                    }
                }
            } catch (e: NullPointerException) {
                log.error("change_port_state(): Illegal url params")
                log.error("change_port_state(): 404 - not enough url params - port and state")
                call.respond(HttpStatusCode.NotFound, "Request must include port and state")
            } catch (e: IllegalArgumentException) {
                log.error("change_port_state(): Illegal argument value ::toInt")
                call.respond(HttpStatusCode.BadRequest, "Check parameters. Port must be number")
            }
        }
    }
}
