package hyunec.airouter.service

import hyunec.airouter.controller.ChatCompletionController
import net.datafaker.Faker
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OpenAIService(
    @Value("\${apikey.openai}") private val apiKey: String,
) {
    private val faker = Faker()
    private val baseUrl = "https://api.openai.com/"

    private val webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeaders {
            it.set(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .build()

    fun chat(request: ChatCompletionController.Request): Mono<String> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String::class.java)
    }

    fun streamChat(request: ChatCompletionController.Request): Flux<String> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String::class.java)
    }
}
