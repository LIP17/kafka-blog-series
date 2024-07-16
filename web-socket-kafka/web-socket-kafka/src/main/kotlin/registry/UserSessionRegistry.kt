package registry

import io.ktor.websocket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

class UserSessionRegistry {

    companion object {
        fun resolveIdentifier(userId: String): String {
            return userId
        }
    }


    private val userSession = ConcurrentHashMap<String, WebSocketSession>()

    fun register(identifier: String, session: WebSocketSession) {
        userSession[identifier] = session
    }

    fun unregister(identifier: String) {
        userSession.remove(identifier);
    }

    fun get(identifier: String): WebSocketSession? {
        return userSession[identifier]
    }
}