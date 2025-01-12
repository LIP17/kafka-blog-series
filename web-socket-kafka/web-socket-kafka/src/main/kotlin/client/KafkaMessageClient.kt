package client

import model.UserMessage
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import serialization.JsonKafkaSerializer

class KafkaMessageClient {

    private val producer = createProducer()

    fun messageReceived(userMessage: UserMessage) {
        // key is not necessary here since consumer will redistribute messages
        producer.send(ProducerRecord("user_message_received", userMessage))
    }

    fun close() {
        producer.flush()
    }

    private fun createProducer(): Producer<String, UserMessage> {
        val config = mutableMapOf<String, Any>()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonKafkaSerializer::class.java

        return KafkaProducer(config)
    }
}
