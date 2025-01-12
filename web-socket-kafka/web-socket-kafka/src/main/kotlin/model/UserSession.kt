package model

data class UserSession (
    val userId: String,
    val host: String,
    val port: Int,
)
