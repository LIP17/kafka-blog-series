import client.SessionRegistryClient
import handler.MessageRedirectHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import model.UserMessage
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import registry.UserSessionRegistry
import serialization.JsonKafkaDeserializer
import serialization.JsonKafkaDeserializer.Companion.JSON_VALUE_TYPE_CONFIG
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MessageDispatcher(
    private val messageRedirectHandler: MessageRedirectHandler,
) {

    private fun buildConsumer(consumerId: String): Consumer<String, UserMessage> {
        val config = mutableMapOf<String, Any>()

        config[ConsumerConfig.CLIENT_ID_CONFIG] = consumerId
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonKafkaDeserializer::class.java
        config[JSON_VALUE_TYPE_CONFIG] = UserMessage::class.java
        config[ConsumerConfig.GROUP_ID_CONFIG] = "message_dispatcher"
        config[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        return KafkaConsumer(config)
    }

    fun runConsumer(consumerId: String, topic: String) {

        val consumer = buildConsumer(consumerId)
        consumer.subscribe(listOf(topic))

        logger.info { "Consumer $consumerId started" }

        try {
            while (true) {
                // Poll for records
                val records: ConsumerRecords<String, UserMessage> = consumer.poll(Duration.ofMillis(1000))

                for (record in records) {
                    runBlocking {
                        messageRedirectHandler.dispatchMessage(record.value())
                    }
                }

                consumer.commitSync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            consumer.close()
            logger.info { "Consumer $consumerId closed" }
        }
    }

    companion object {

        private val logger = KotlinLogging.logger { MessageDispatcher::class.java.name }
        private val httpClient = HttpClient(CIO) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json() // Configure the JSON library
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val numOfPartitions = 3
            val topic = "user_message_received"

            val executorService = Executors.newFixedThreadPool(numOfPartitions)
            val dispatcher = MessageDispatcher(
                MessageRedirectHandler(
                    UserSessionRegistry(),
                    SessionRegistryClient(),
                    httpClient
                )
            )

            repeat(numOfPartitions) { consumerId ->
                executorService.submit { dispatcher.runConsumer(consumerId.toString(), topic) }
            }

            Runtime.getRuntime().addShutdownHook(
                Thread {
                    executorService.shutdown()
                    executorService.awaitTermination(10, TimeUnit.SECONDS)
                    httpClient.close()
                }
            )
        }
    }
}
