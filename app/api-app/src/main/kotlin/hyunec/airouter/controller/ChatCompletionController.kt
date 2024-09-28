package hyunec.airouter.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import hyunec.airouter.service.OpenAIService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ChatCompletionController(
    private val openAIService: OpenAIService
) {
    @PostMapping("/api/chat/completions")
    fun chatCompletion(@RequestBody request: Request): ResponseEntity<out Flux<out String>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(openAIService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(openAIService.chat(request).flux())
    }

    data class Request(
        val model: String,
        val messages: List<Message>,
        val stream: Boolean = false
    ) {
        data class Message(
            val role: String,
            val content: String
        )
    }

    sealed class Response {
        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class NonStreamChoice(
            val index: Int,
            val message: Message,
            val finishReason: String?
        )

        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class StreamChoice(
            val index: Int,
            val delta: Delta,
            val finishReason: String?
        )

        data class Message(
            val role: String,
            val content: String
        )

        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class Usage(
            val promptTokens: Int,
            val completionTokens: Int,
            val totalTokens: Int
        )

        data class Delta(
            val content: String? = null
        )
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class NonStreamResponse(
        val id: String,
        @JsonProperty("object")
        val objectz: String,
        val created: Long,
        val model: String,
        val choices: List<NonStreamChoice>,
        val usage: Usage,
        val systemFingerprint: String
    ) : Response()

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class StreamResponse(
        val id: String,
        @JsonProperty("object")
        val objectz: String,
        val created: Long,
        val model: String,
        val choices: List<StreamChoice>
    ) : Response()
}
