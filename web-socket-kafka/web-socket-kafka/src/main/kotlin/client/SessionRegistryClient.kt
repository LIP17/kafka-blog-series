package client

import com.fasterxml.jackson.module.kotlin.readValue
import constants.Constants.HOST_NAME
import constants.Constants.SESSION_REGISTRY_PORT
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.host
import io.ktor.client.request.port
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.path
import model.SessionRegistryRequest
import model.UserSession
import serialization.JsonMapper

class SessionRegistryClient {

    private val logger = KotlinLogging.logger { SessionRegistryClient::class.java.name }
    private val httpClient = HttpClient(CIO)

    suspend fun registerSessionHost(userId: String, host: String, port: Int) {
        val resp = httpClient.post {
            this.host = HOST_NAME
            this.port = SESSION_REGISTRY_PORT
            this.setBody(JsonMapper.objectMapper.writeValueAsString(SessionRegistryRequest(userId, host, port)))
            this.url {
                path("register-user-session")
            }
        }

        logger.info { "Register session respond status ${resp.status}" }
    }

    suspend fun unregisterSessionHost(userId: String, host: String, port: Int) {
        val resp = httpClient.delete {
            this.host = HOST_NAME
            this.port = SESSION_REGISTRY_PORT
            this.setBody(JsonMapper.objectMapper.writeValueAsString(SessionRegistryRequest(userId, host, port)))
        }

        logger.info { "Unregister session respond status ${resp.status}" }
    }

    suspend fun getUserSessions(userId: String): List<UserSession> {
        val resp = httpClient.get {
            this.host = HOST_NAME
            this.port = SESSION_REGISTRY_PORT
            this.url {
                path("get-user-session", userId)
            }
        }

        return JsonMapper.objectMapper.readValue<List<UserSession>>(resp.bodyAsText())
    }
}
