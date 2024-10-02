package hyunec.airouter.coredomain.dto

class OpenAiChatRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false
) {
    data class Message(
        val role: String,
        val content: String
    )
}
