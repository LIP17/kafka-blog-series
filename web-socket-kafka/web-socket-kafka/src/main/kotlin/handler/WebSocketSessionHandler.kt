package handler

import WebSessionConstants.USER_ID
import client.KafkaMessageClient
import client.SessionRegistryClient
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.readReason
import model.UserMessage
import registry.WebSocketSessionRegistry
import serialization.JsonMapper


class WebSocketSessionHandler(
    private val sessionRegistryClient: SessionRegistryClient,
    private val webSocketSessionRegistry: WebSocketSessionRegistry,
    private val kafkaMessageClient: KafkaMessageClient,
    private val hostName: String,
    private val port: Int,
) {

    companion object {
        private val logger = KotlinLogging.logger { WebSocketSessionHandler::class.java.name }
    }

    suspend fun handle(session: WebSocketServerSession) {
        val userId = session.call.parameters[USER_ID] ?: throw MissingRequestParameterException(USER_ID)
        sessionRegistryClient.registerSessionHost(userId, hostName, port)
        webSocketSessionRegistry.registerSession(userId, session)
        handleIncomingMessage(session, userId)
        confirmReady(session, userId)
    }

    private suspend fun confirmReady(session: WebSocketServerSession, userId: String) {
        session.send(Frame.Text("Websocket Session Created"))
        logger.info { "Session Created With User $userId" }
    }

    private suspend fun handleIncomingMessage(session: WebSocketServerSession, userId: String) {
        for (frame in session.incoming) {
            when (frame) {
                is Frame.Text -> {
                    val userMessage: UserMessage = JsonMapper.objectMapper.readValue(frame.readText())
                    kafkaMessageClient.messageReceived(userMessage)
                }

                is Frame.Close -> {
                    logger.info { "Session Closed With User $userId, ${frame.readReason()}" }
                    unregisterSession(userId)
                    return
                }

                else -> {
                    logger.info { "Received unused type of frame from user $userId" }
                    unregisterSession(userId)
                    return
                }
            }
        }
    }

    private suspend fun unregisterSession(userId: String) {
        webSocketSessionRegistry.unregisterSession(userId)
        sessionRegistryClient.unregisterSessionHost(userId, hostName, port)
    }
}
