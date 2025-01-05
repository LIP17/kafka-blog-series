package handler

import client.KafkaMessageClient
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import model.UserMessage
import registry.UserSessionRegistry
import serialization.JsonMapper

class MessageRedirectHandler (
    private val userSessionRegistry: UserSessionRegistry,
    private val kafkaMessageClient: KafkaMessageClient
) {
    companion object {
        private val logger = KotlinLogging.logger { MessageRedirectHandler::class.java.name }
    }

    suspend fun handle(call: ApplicationCall) {
        val rawMessage = call.receiveText()
        val userMessage = JsonMapper.objectMapper.readValue<UserMessage>(rawMessage)
        val userSession = userSessionRegistry.get(userMessage.receiverId)

        if (userSession == null) {
            logger.info { "Enqueue pending message" }
            kafkaMessageClient.userMessagePending(userMessage)
            return
        }

        logger.info { "Redirect message from ${userMessage.senderId} to ${userMessage.receiverId}" }
        userSession.send(Frame.Text(JsonMapper.objectMapper.writeValueAsString(userMessage)))

        call.respond(HttpStatusCode.OK)
    }
}