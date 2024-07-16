import handler.WebSocketSessionHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import registry.UserSessionRegistry
import java.time.Duration

object ServerStarter {

    private val userSessionRegistry = UserSessionRegistry()
    private val newSessionHandler = WebSocketSessionHandler(userSessionRegistry)

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(Netty, port = 8080) {
            install(WebSockets) {
                pingPeriod = Duration.ofMinutes(1)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                webSocket("/web-socket") {
                    newSessionHandler.handle(this)
                }
            }
        }.start(wait = true)
    }
}