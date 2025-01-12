package registry

import io.ktor.websocket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

class WebSocketSessionRegistry {

    private val userSessionMap = ConcurrentHashMap<String, WebSocketSession>()

    fun registerSession(userId: String, session: WebSocketSession) {
        userSessionMap[userId] = session
    }

    fun getSession(userId: String): WebSocketSession? {
        return userSessionMap[userId]
    }

    fun unregisterSession(userId: String) {
        userSessionMap.remove(userId)
    }
}