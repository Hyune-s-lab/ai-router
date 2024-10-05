package hyunec.airouter.coredomain.chat

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import hyunec.airouter.coredomain.chat.dto.ChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AnthropicWebClientService(
    @Qualifier("anthropicWebClient") private val webClient: WebClient
) : ChatService {
    private final val log = KotlinLogging.logger {}

    private val mapper = jacksonObjectMapper().registerKotlinModule()
    private final val defaultMaxTokens = 1024

    override fun chat(request: ChatRequest): Mono<out Any> {
        log.debug { "### webclient chat" }

        return webClient.post()
            .uri("/v1/messages")
            .bodyValue(request.toBody())
            .retrieve()
            .bodyToMono()
    }

    override fun streamChat(request: ChatRequest): Flux<out Any> {
        log.debug { "### webclient streamChat" }

        return webClient.post()
            .uri("/v1/messages")
            .bodyValue(request.toBody())
            .retrieve()
            .bodyToFlux()
    }

    fun ChatRequest.toBody(): String {
        return mapper.writeValueAsString(
            mapOf(
                "model" to model,
                "messages" to messages,
                "max_tokens" to defaultMaxTokens,
                "stream" to stream
            )
        )
    }
}
