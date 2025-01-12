package serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

object JsonMapper {
    val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}

class JsonKafkaSerializer<T>: Serializer<T> {
    override fun serialize(topic: String, data: T?): ByteArray? {
        if (data == null) return null

        return try {
            JsonMapper.objectMapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            throw RuntimeException("Failed to serialize object to JSON: $data", e)
        }
    }
}

class JsonKafkaDeserializer<T>: Deserializer<T> {

    companion object {
        const val JSON_VALUE_TYPE_CONFIG = "json_value.type"
    }

    private lateinit var targetType: Class<T>
    // used by consumer
    constructor()


    override fun configure(configs: Map<String, *>, isKey: Boolean) {
        try {
            @Suppress("UNCHECKED_CAST")
            targetType = configs[JSON_VALUE_TYPE_CONFIG] as Class<T>
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("Class not found for deserialization: ${configs[JSON_VALUE_TYPE_CONFIG]}", e)
        }
    }

    override fun deserialize(topic: String, data: ByteArray): T {
        return JsonMapper.objectMapper.readValue(data, targetType)
    }
}
