import client.KafkaMessageClient
import client.SessionRegistryClient
import constants.Constants.HOST_NAME
import constants.Constants.SESSION_REGISTRY_PORT
import handler.SendMessageHandler
import handler.WebSocketSessionHandler
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import registry.WebSocketSessionRegistry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object ChatServer {
    @JvmStatic
    fun main(args: Array<String>) {
        val kafkaMessageClient = KafkaMessageClient()
        val webSocketSessionRegistry = WebSocketSessionRegistry()
        val sessionRegistryClient = SessionRegistryClient()
        val port = args[0].toInt()

        val newSessionHandler = WebSocketSessionHandler(
            sessionRegistryClient,
            webSocketSessionRegistry,
            kafkaMessageClient,
            HOST_NAME,
            port,
        )

        val sendMessageHandler = SendMessageHandler(
            webSocketSessionRegistry,
        )

        embeddedServer(Netty, port = port) { // pass dynamic port number for different instance
            install(WebSockets) {
                pingPeriod = 10.seconds
                timeout = 15.seconds
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                webSocket("/web-socket") {
                    newSessionHandler.handle(this)
                }

                post("/send-message") {
                    sendMessageHandler.handle(this.call)
                }
            }
        }.apply {
            addShutdownHook {
                kafkaMessageClient.close()
            }
        }.start(wait = true)
    }

}
