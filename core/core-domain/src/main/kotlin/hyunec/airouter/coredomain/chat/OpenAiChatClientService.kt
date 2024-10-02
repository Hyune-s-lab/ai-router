package hyunec.airouter.coredomain.chat

import hyunec.airouter.coredomain.chat.dto.ChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OpenAiChatClientService(
    private val openAiApi: OpenAiApi
) : ChatService {
    private final val log = KotlinLogging.logger {}

    override fun chat(request: ChatRequest): Mono<out Any> {
        return ChatClient.builder(OpenAiApiService(openAiApi))
            .build()
            .prompt(request.toPrompt())
            .call()
            .chatResponse()
            .run {
                Mono.just(this)
            }
    }

    override fun streamChat(request: ChatRequest): Flux<out Any> {
        return ChatClient.builder(OpenAiApiService(openAiApi))
            .build()
            .prompt(request.toPrompt())
            .stream()
            .chatResponse()
            .map { it as Any }
            .concatWith(Flux.just("[DONE]"))
    }
}
