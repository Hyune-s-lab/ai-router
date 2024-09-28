package hyunec.airouter.service

class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false
) {
    data class Message(
        val role: String,
        val content: String
    )
}
