package model

data class UserMessage (
    val senderId: String,
    val receiverId: String,
    val message: String,
)
