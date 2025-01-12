package model

data class SessionRegistryRequest(
    val userId: String,
    val host: String,
    val port: Int
)
