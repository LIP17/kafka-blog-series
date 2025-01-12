package handler

import client.SessionRegistryClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.host
import io.ktor.client.request.port
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import model.UserMessage
import model.UserSession
import registry.UserSessionRegistry
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.path
import serialization.JsonMapper

class MessageRedirectHandler(
    private val sessionRegistry: UserSessionRegistry,
    private val sessionRegistryClient: SessionRegistryClient,
    private val httpClient: HttpClient,
) {

    companion object {
        private val logger = KotlinLogging.logger { MessageRedirectHandler::class.java.name }
    }

    /**
     * Locate any existing session and send the message out
     */
    suspend fun dispatchMessage(message: UserMessage) {
        val userSessions = fetchUserSessions(message.receiverId)

        if (userSessions.isEmpty()) {
            return
        }

        userSessions.map { userSession ->
            val resp = httpClient.post {
                this.contentType(io.ktor.http.ContentType.Application.Json)
                this.host = userSession.host
                this.port = userSession.port
                this.setBody(JsonMapper.objectMapper.writeValueAsString(message))
                url {
                    path("send-message")
                }
            }
            logger.info { "Redirect message response status ${resp.status}" }
            if (resp.status == HttpStatusCode.NotFound) {
                sessionRegistry.unregisterUser(userSession)
            }
        }
    }

    private suspend fun fetchUserSessions(userId: String): List<UserSession> {
        val cachedUserSessions = sessionRegistry.getUserSessions(userId)
        if (cachedUserSessions.isNotEmpty()) return cachedUserSessions

        val userSessions = sessionRegistryClient.getUserSessions(userId)

        return userSessions.onEach { sessionRegistry.registerUserSession(it) }
    }
}
