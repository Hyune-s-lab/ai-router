package hyunec.airouter.coredomain.chat

import hyunec.airouter.coredomain.chat.dto.ChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OpenAiWebClientService(
    private val webClient: WebClient
) : ChatService {
    private final val log = KotlinLogging.logger {}

    override fun chat(request: ChatRequest): Mono<out Any> {
        log.debug { "### webclient chat" }

        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono()
    }

    override fun streamChat(request: ChatRequest): Flux<out Any> {
        log.debug { "### webclient streamChat" }

        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(String::class.java)
    }
}
