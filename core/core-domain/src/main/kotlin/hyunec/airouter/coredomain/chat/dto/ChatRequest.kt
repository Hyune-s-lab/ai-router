package hyunec.airouter.coredomain.chat.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import hyunec.airouter.coredomain.common.AiProvider
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.ChatOptionsBuilder
import org.springframework.ai.chat.prompt.Prompt

data class ChatRequest(
    val model: Model,
    val messages: List<Message>,
    val stream: Boolean = false
) {
    data class Message(
        val role: MessageType,
        val content: String
    )

    enum class Model(
        @JsonValue val value: String,
        val provider: AiProvider
    ) {
        // openai
        GPT_4O("gpt-4o", AiProvider.OPENAI),

        // anthropic
        CLAUDE_3_5_SONNET_20240620("claude-3-5-sonnet-20240620", AiProvider.ANTHROPIC),
        ;

        @JsonCreator
        fun fromValue(value: String): Model {
            return entries.firstOrNull {
                it.value == value
            } ?: throw IllegalArgumentException("Unsupported model: $value")
        }
    }

    enum class MessageType(
        @JsonValue val value: String
    ) {
        USER("user"),
        ASSISTANT("assistant"),
        SYSTEM("system"),
        TOOL("tool");

        @JsonCreator
        fun fromValue(value: String): org.springframework.ai.chat.messages.MessageType {
            org.springframework.ai.chat.messages.MessageType.entries.first() {
                it.value == value
            }

            throw IllegalArgumentException("Invalid MessageType value: $value")
        }
    }

    fun toPrompt(): Prompt {
        val chatOptions = ChatOptionsBuilder.builder()
            .withModel(this.model.value)
            .build()
        val messages = this.messages.map {
            when (it.role) {
                MessageType.USER -> UserMessage(it.content)
                MessageType.ASSISTANT -> AssistantMessage(it.content)
                MessageType.SYSTEM -> SystemMessage(it.content)
                MessageType.TOOL -> throw UnsupportedOperationException("Tool message is not supported")
            }
        }
        return Prompt(messages, chatOptions)
    }
}
