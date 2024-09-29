package hyunec.airouter.chatdomain.port

import hyunec.airouter.chatdomain.config.OpenAiConfig
import hyunec.airouter.chatdomain.dto.OpenAiChatRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OpenAiChatWebClientAdapter(
    config: OpenAiConfig
) : OpenAiChatPort {
    private val webClient = WebClient.builder()
        .baseUrl(config.baseUrl)
        .defaultHeaders {
            it.set(HttpHeaders.AUTHORIZATION, "Bearer ${config.apiKey}")
            it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        .build()

    override fun chat(request: OpenAiChatRequest): Mono<out Any> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono()
    }

    override fun streamChat(request: OpenAiChatRequest): Flux<out Any> {
        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String::class.java)
    }
}
