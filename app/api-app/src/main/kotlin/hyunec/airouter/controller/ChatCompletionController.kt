package hyunec.airouter.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import net.datafaker.Faker
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ChatCompletionController {

    private val faker = Faker()

    @PostMapping("/api/chat/completions")
    fun chatCompletion(@RequestBody request: Request): ResponseEntity<Flux<Response>> {
//        if (request.stream) {
//            return ResponseEntity.ok()
//                .contentType(MediaType.TEXT_EVENT_STREAM)
//                .body(
//                    Flux.interval(Duration.ofMillis(100))
//                        .take(5)
//                        .map {
//                            """{"id":"chatcmpl-ACSoUZd7DWc08oRwdvzuTVjSMbqRY","object":"chat.completion.chunk","created":${System.currentTimeMillis()},"model":"gpt-4o-2024-05-13","system_fingerprint":"fp_057232b607","choices":[{"index":0,"delta":{"role":"assistant","content":"","refusal":null},"logprobs":null,"finish_reason":null}]}"""
//                        }
//                )
//        }
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Flux.just(nonStreamResponseStub(request)))
    }

    private fun nonStreamResponseStub(request: Request): NonStreamResponse {
        return NonStreamResponse(
            id = faker.number().digits(10),
            objectz = faker.lorem().word(),
            created = System.currentTimeMillis(),
            model = request.model,
            choices = listOf(
                Response.Choice(
                    index = 0,
                    message = Response.MessageResponse(
                        role = "assistant",
                        content = faker.lorem().sentence()
                    ),
                    finishReason = "stop"
                )
            ),
            usage = Response.Usage(
                promptTokens = faker.number().numberBetween(0, 100),
                completionTokens = faker.number().numberBetween(0, 100),
                totalTokens = faker.number().numberBetween(0, 100)
            ),
            systemFingerprint = faker.lorem().word()
        )
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
        data class Choice(
            val index: Int,
            val message: MessageResponse,
            val finishReason: String?
        )

        data class MessageResponse(
            val role: String,
            val content: String
        )

        @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class Usage(
            val promptTokens: Int,
            val completionTokens: Int,
            val totalTokens: Int
        )
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class NonStreamResponse(
        val id: String,
        @JsonProperty("object")
        val objectz: String,
        val created: Long,
        val model: String,
        val choices: List<Choice>,
        val usage: Usage,
        val systemFingerprint: String
    ) : Response()
}
