import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.gson.*
import model.UpdateStorage

fun Application.runServer() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter())
    }
    routing {
        get("/gps") {
            call.respond(UpdateStorage.getUpdate())
        }
    }
}