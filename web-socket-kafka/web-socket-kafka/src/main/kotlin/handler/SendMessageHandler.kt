package handler

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond

import io.ktor.websocket.Frame
import model.UserMessage
import registry.WebSocketSessionRegistry
import serialization.JsonMapper

class SendMessageHandler (
    private val webSocketSessionRegistry: WebSocketSessionRegistry
) {
    companion object {
        private val logger = KotlinLogging.logger { SendMessageHandler::class.java.name }
    }

    suspend fun handle(call: ApplicationCall) {
        val rawMessage = call.receiveText()
        val userMessage = JsonMapper.objectMapper.readValue<UserMessage>(rawMessage)
        val userSession = webSocketSessionRegistry.getSession(userMessage.receiverId)
            ?: return call.respond(HttpStatusCode.NotFound)

        logger.info { "Redirect message from ${userMessage.senderId} to ${userMessage.receiverId}" }
        userSession.send(Frame.Text(JsonMapper.objectMapper.writeValueAsString(userMessage)))

        call.respond(HttpStatusCode.OK)
    }
}
