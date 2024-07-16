package registry

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

class UserSessionRegistry {

    companion object {
        private val logger = KotlinLogging.logger { UserSessionRegistry::class.java.name }
        fun resolveIdentifier(userId: String): String {
            return userId
        }
    }


    private val userSession = ConcurrentHashMap<String, WebSocketSession>()

    fun registerUser(userId: String, session: WebSocketSession) {
        val identifier = resolveIdentifier(userId)
        logger.info { "Register session with identifier $identifier" }
        userSession[identifier] = session
    }

    fun unregisterUser(userId: String) {
        val identifier = resolveIdentifier(userId)
        userSession.remove(identifier);
    }

    fun get(identifier: String): WebSocketSession? {
        return userSession[identifier]
    }
}