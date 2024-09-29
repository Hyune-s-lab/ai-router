package hyunec.airouter.chatdomain.port

import com.fasterxml.jackson.databind.ObjectMapper
import hyunec.airouter.chatdomain.config.OpenAiConfig
import hyunec.airouter.chatdomain.dto.OpenAiChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.Role
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OpenAiChatSpringAiAdapter(
    config: OpenAiConfig,
) : OpenAiChatPort {

    private val openAiApi = OpenAiApi(config.apiKey)

    private val objectMapper = ObjectMapper()
    private final val log = KotlinLogging.logger {}

    override fun chat(request: OpenAiChatRequest): Mono<out Any> {
        log.debug { "### springAi chat" }

        val messages = request.messages.map {
            ChatCompletionMessage(it.content, Role.USER)
        }
        val chatCompletionRequest = OpenAiApi.ChatCompletionRequest(
            messages, request.model, null
        )

        return openAiApi.chatCompletionEntity(chatCompletionRequest).run {
            Mono.just(this.body!!)
        }
    }

    override fun streamChat(request: OpenAiChatRequest): Flux<out Any> {
        log.debug { "### springAi streamChat" }

        val messages = request.messages.map {
            ChatCompletionMessage(it.content, Role.USER)
        }
        val chatCompletionRequest = OpenAiApi.ChatCompletionRequest(
            messages, request.model,
            null, null, null, null, null, null, null,
            null, null, null, true, null, null, null,
            null, null, null, null
        )

        return openAiApi.chatCompletionStream(chatCompletionRequest)
            .map { it as Any}
            .concatWith(Flux.just("[DONE]"))
    }
}
