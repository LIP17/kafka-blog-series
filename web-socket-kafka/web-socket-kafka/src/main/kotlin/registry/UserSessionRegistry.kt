package registry

import io.github.oshai.kotlinlogging.KotlinLogging
import model.UserSession
import java.util.concurrent.ConcurrentHashMap

class UserSessionRegistry {

    companion object {
        private val logger = KotlinLogging.logger { UserSessionRegistry::class.java.name }
    }

    // 1 user could have multiple device, hence multiple active sessions
    private val userSessionMap = ConcurrentHashMap<String, MutableSet<UserSession>>()

    fun registerUserSession(session: UserSession) {
        logger.info { "Register session for user ${session.userId}" }
        userSessionMap.compute(session.userId) { _, sessions ->
            val updatedSessions = sessions ?: mutableSetOf()
            if (!updatedSessions.add(session)) {
                // Session was already present, so we return the same set
                return@compute updatedSessions
            }
            updatedSessions
        }
    }

    // one user can have multiple active sessions
    fun getUserSessions(userId: String): List<UserSession> {
        return userSessionMap[userId]?.toList() ?: listOf()
    }

    fun listAllSessions(): List<UserSession> {
        return userSessionMap.flatMap { it.value.toList() }
    }

    fun unregisterUser(session: UserSession) {
        logger.info { "Unregister session for user session $session " }
        userSessionMap.computeIfPresent(session.userId) { _, sessions ->
            sessions.apply { sessions.remove(session) }
        }
    }
}
