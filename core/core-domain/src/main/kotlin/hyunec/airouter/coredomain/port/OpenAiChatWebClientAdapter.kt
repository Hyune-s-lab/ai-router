package hyunec.airouter.coredomain.port

import hyunec.airouter.coredomain.config.OpenAiConfig
import hyunec.airouter.coredomain.dto.OpenAiChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
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

    private final val log = KotlinLogging.logger {}

    override fun chat(request: OpenAiChatRequest): Mono<out Any> {
        log.debug { "### webclient chat" }

        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono()
    }

    override fun streamChat(request: OpenAiChatRequest): Flux<out Any> {
        log.debug { "### webclient streamChat" }

        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String::class.java)
    }
}
