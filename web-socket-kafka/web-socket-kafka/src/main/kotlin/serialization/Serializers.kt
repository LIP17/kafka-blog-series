package serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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