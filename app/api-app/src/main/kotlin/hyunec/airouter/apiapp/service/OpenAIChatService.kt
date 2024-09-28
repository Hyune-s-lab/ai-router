package hyunec.airouter.apiapp.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OpenAIChatService(
    @Value("\${apikey.openai}") private val apiKey: String,
) {
    private val baseUrl = "https://api.openai.com/"

    private val webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeaders {
            it.set(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .build()

    fun chat(request: ChatRequest): Mono<String> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String::class.java)
    }

    fun streamChat(request: ChatRequest): Flux<String> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String::class.java)
    }
}
