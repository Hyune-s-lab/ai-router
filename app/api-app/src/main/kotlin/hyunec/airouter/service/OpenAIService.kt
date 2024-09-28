package hyunec.airouter.service

import hyunec.airouter.controller.ChatCompletionController
import hyunec.airouter.controller.ChatCompletionController.*
import net.datafaker.Faker
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.time.Duration

@Service
class OpenAIService(
    @Value("\${apikey.openai}") private val apiKey: String,
) {
    private val faker = Faker()
    private val baseUrl = "https://api.openai.com/"

    private val webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Authorization", "Bearer $apiKey")
        .defaultHeader("Content-Type", "application/json")
        .build()

//    fun chat(request: ChatCompletionController.Request): Flux<String> {
//        return webClient.post()
//            .uri("/v1/chat/completions")
//            .bodyValue(request)
//            .retrieve()
//            .bodyToFlux(String::class.java)
//    }

    fun chat(request: ChatCompletionController.Request): NonStreamResponse {
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

//    fun streamChat(request: ChatCompletionController.Request): Flux<String> {
//        return webClient.post()
//            .uri("/v1/chat/completions")
//            .bodyValue(request)
//            .retrieve()
//            .bodyToFlux(String::class.java)
//    }

    fun streamChat(request: ChatCompletionController.Request): Flux<StreamResponse> {
        return Flux.interval(Duration.ofMillis(100))
            .take(5)
            .map { streamResponseStub(request, false) }
            .concatWith(
                Flux.just(streamResponseStub(request, true))
            )
    }

    fun streamResponseStub(request: Request, isLast: Boolean): StreamResponse {
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
}
