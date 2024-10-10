package hyunec.airouter.coredomain.chat

import hyunec.airouter.coredomain.chat.dto.ChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.anthropic.AnthropicChatModel
import org.springframework.ai.anthropic.api.AnthropicApi
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AnthropicApiService(
    anthropicApi: AnthropicApi
) : ChatService, AnthropicChatModel(anthropicApi) {
    private final val log = KotlinLogging.logger {}

    override fun chat(request: ChatRequest): Mono<out Any> {
        return ChatClient.builder(AnthropicApiService(anthropicApi))
            .build()
            .prompt(request.toPrompt())
            .call()
            .chatResponse()
            .run {
                Mono.just(this)
            }
    }

    override fun streamChat(request: ChatRequest): Flux<out Any> {
        return ChatClient.builder(AnthropicApiService(anthropicApi))
            .build()
            .prompt(request.toPrompt())
            .stream()
            .chatResponse()
            .map { it as Any }
    }
}
