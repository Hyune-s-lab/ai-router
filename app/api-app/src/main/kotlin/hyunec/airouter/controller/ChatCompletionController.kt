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
import java.time.Duration

@RestController
class ChatCompletionController {

    private val faker = Faker()

    @PostMapping("/api/chat/completions")
    fun chatCompletion(@RequestBody request: Request): ResponseEntity<out Flux<out Response>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(
                    Flux.interval(Duration.ofMillis(100))
                        .take(5)
                        .map { streamResponseStub(request, false) }
                        .concatWith(
                            Flux.just(streamResponseStub(request, true))
                        )
                )
        }

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
                Response.NonStreamChoice(
                    index = 0,
                    message = Response.Message(
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

    private fun streamResponseStub(request: Request, isLast: Boolean): StreamResponse {
        return StreamResponse(
            id = faker.number().digits(10),
            objectz = faker.lorem().word(),
            created = System.currentTimeMillis(),
            model = request.model,
            choices = listOf(
                Response.StreamChoice(
                    index = 0,
                    delta = Response.Delta(
                        content = if (isLast) null else faker.lorem().sentence()
                    ),
                    finishReason = if (isLast) "stop" else null
                )
            )
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
