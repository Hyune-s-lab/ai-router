package hyunec.airouter.chatdomain.port

import hyunec.airouter.chatdomain.config.OpenAIConfig
import hyunec.airouter.chatdomain.dto.OpenAIChatRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OpenAIChatWebClientAdapter(
    config: OpenAIConfig
) : OpenAIChatPort {
    private val webClient = WebClient.builder()
        .baseUrl(config.baseUrl)
        .defaultHeaders {
            it.set(HttpHeaders.AUTHORIZATION, "Bearer ${config.apiKey}")
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .build()

    override fun chat(request: OpenAIChatRequest): Mono<String> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String::class.java)
    }

    override fun streamChat(request: OpenAIChatRequest): Flux<String> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String::class.java)
    }
}
