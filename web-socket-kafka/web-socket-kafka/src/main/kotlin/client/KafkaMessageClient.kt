package client

import model.UserMessage
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import serialization.JsonKafkaSerializer

class KafkaMessageClient {

    private val template = createKafkaTemplate()

    fun messageReceived(userMessage: UserMessage) {
        // key is not necessary here since consumer will redistribute messages
        template.send("user_message_received", userMessage)
    }

    fun userMessagePending(userMessage: UserMessage) {
        template.send("user_message_pending", userMessage)
    }

    fun close() {
        template.flush()
    }

    private fun createKafkaTemplate(): KafkaTemplate<String, UserMessage> {
        return KafkaTemplate(createMessageProducerFactory())
    }

    private fun createMessageProducerFactory(): ProducerFactory<String, UserMessage> {
        val config = mutableMapOf<String, Any>()

        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonKafkaSerializer::class.java

        return DefaultKafkaProducerFactory(config)
    }
}
