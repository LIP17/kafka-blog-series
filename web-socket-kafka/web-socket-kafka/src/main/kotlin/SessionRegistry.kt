import com.fasterxml.jackson.module.kotlin.readValue
import constants.Constants.SESSION_REGISTRY_PORT
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import model.UserSession
import registry.UserSessionRegistry
import serialization.JsonMapper

object SessionRegistry {

    @JvmStatic
    fun main(args: Array<String>) {

        val userSessionRegistry = UserSessionRegistry()

        embeddedServer(Netty, port = SESSION_REGISTRY_PORT) {
            routing {
                get("/get-user-session/{id}") {
                    val userSessions = userSessionRegistry.getUserSessions(this.call.parameters["id"]!!)
                    this.call.respond(HttpStatusCode.OK, JsonMapper.objectMapper.writeValueAsString(userSessions))
                }
                get("/list-user-sessions") {
                    val userSessions = userSessionRegistry.listAllSessions()
                    this.call.respond(HttpStatusCode.OK, JsonMapper.objectMapper.writeValueAsString(userSessions))
                }
                post("/register-user-session") {
                    userSessionRegistry.registerUserSession(extractUserSession(this.call))
                    this.call.respond(HttpStatusCode.OK)
                }
                delete("/unregister-user-session") {
                    userSessionRegistry.unregisterUser(extractUserSession(this.call))
                    this.call.respond(HttpStatusCode.OK)
                }
            }
        }.start(wait = true)
    }

    private suspend fun extractUserSession(call: ApplicationCall): UserSession {
        val rawMessage = call.receiveText()
        return JsonMapper.objectMapper.readValue<UserSession>(rawMessage)

    }
}
