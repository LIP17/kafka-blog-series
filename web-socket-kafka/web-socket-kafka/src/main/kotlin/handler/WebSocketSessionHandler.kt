package handler

import WebSessionConstants.USER_ID
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import registry.UserSessionRegistry
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.readReason


class WebSocketSessionHandler(
    private val userSessionRegistry: UserSessionRegistry
) {

    companion object {
        private val logger = KotlinLogging.logger { WebSocketSessionHandler::class.java.name }
    }

    suspend fun handle(session: WebSocketServerSession) {
        val userId = session.call.parameters[USER_ID] ?: throw MissingRequestParameterException(USER_ID)
        userSessionRegistry.registerUser(userId, session)
        confirmReady(session, userId)
        handleIncomingMessage(session, userId)
    }

    private suspend fun confirmReady(session: WebSocketServerSession, userId: String) {
        session.send(Frame.Text("Session Created"))
        logger.info { "Session Created With User $userId" }
    }

    private suspend fun handleIncomingMessage(session: WebSocketServerSession, userId: String) {
        for (frame in session.incoming) {
            when (frame) {
                is Frame.Text -> {
                    val receivedText = frame.readText()
                    session.send(Frame.Text("You send $receivedText"))
                }

                is Frame.Close -> {
                    logger.info { "Session Closed With User $userId, ${frame.readReason()}" }
                    userSessionRegistry.unregisterUser(userId)
                    return
                }

                else -> {
                    logger.info { "Received unused type of frame from user $userId" }
                }
            }


        }
    }
}