package hyunec.airouter.coredomain.chat

import hyunec.airouter.coredomain.chat.dto.ChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.messages.*
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.ModelOptionsUtils
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.ChatCompletionFunction
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.Role
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.StreamOptions
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Consumer

@Service
class OpenAiApiService(
    private val openAiApi: OpenAiApi
) : ChatService, OpenAiChatModel(openAiApi) {
    private final val log = KotlinLogging.logger {}

    override fun chat(request: ChatRequest): Mono<out Any> {
        log.debug { "### springAi chat" }

        val chatCompletionRequest = createRequest(request.toPrompt(), false)

        return openAiApi.chatCompletionEntity(chatCompletionRequest).run {
            Mono.just(this.body!!)
        }
    }

    override fun streamChat(request: ChatRequest): Flux<out Any> {
        log.debug { "### springAi streamChat" }

        val chatCompletionRequest = createRequest(request.toPrompt(), true)

        return openAiApi.chatCompletionStream(chatCompletionRequest)
            .map { it as Any }
            .concatWith(Flux.just("[DONE]"))
    }

    /**
     * spring-ai 에 package-private 로 선언되어 있는 로직 입니다.
     * - image, function call 로직은 disable 했습니다.
     */
    fun createRequest(prompt: Prompt, stream: Boolean): ChatCompletionRequest {
        val chatCompletionMessages = prompt.instructions.stream().map { message: Message ->
            if (message.messageType != MessageType.USER && message.messageType != MessageType.SYSTEM) {
                when (message.messageType) {
                    MessageType.ASSISTANT -> {
                        val assistantMessage = message as AssistantMessage
                        var toolCalls: List<ChatCompletionMessage.ToolCall>? = null
                        if (!CollectionUtils.isEmpty(assistantMessage.toolCalls)) {
                            toolCalls = assistantMessage.toolCalls.stream().map { toolCall: AssistantMessage.ToolCall ->
                                val function = ChatCompletionFunction(toolCall.name(), toolCall.arguments())
                                ChatCompletionMessage.ToolCall(toolCall.id(), toolCall.type(), function)
                            }.toList()
                        }

                        return@map java.util.List.of<ChatCompletionMessage>(
                            ChatCompletionMessage(
                                assistantMessage.content,
                                Role.ASSISTANT,
                                null as String?,
                                null as String?,
                                toolCalls,
                                null as String?
                            )
                        )
                    }

                    MessageType.TOOL -> {
                        val toolMessage = message as ToolResponseMessage
                        toolMessage.responses.forEach(Consumer { response: ToolResponse ->
                            Assert.isTrue(response.id() != null, "ToolResponseMessage must have an id")
                            Assert.isTrue(response.name() != null, "ToolResponseMessage must have a name")
                        })
                        return@map toolMessage.responses.stream().map<ChatCompletionMessage> { tr: ToolResponse ->
                            ChatCompletionMessage(
                                tr.responseData(),
                                Role.TOOL,
                                tr.name(),
                                tr.id(),
                                null,
                                null as String?
                            )
                        }.toList()
                    }

                    else -> {
                        throw IllegalArgumentException("Unsupported message type: " + message.messageType)
                    }
                }
            } else {
                var content: Any? = message.content
                if (message is UserMessage) {
                    val userMessage = message
//                    if (!CollectionUtils.isEmpty(userMessage.media)) {
//                        val contentList: MutableList<ChatCompletionMessage.MediaContent?> =
//                            ArrayList<Any?>(java.util.List.of(ChatCompletionMessage.MediaContent(message.getContent())))
//                        contentList.addAll(userMessage.media.stream().map { media: Media ->
//                            ChatCompletionMessage.MediaContent(
//                                ImageUrl(
//                                    this.fromMediaData(media.mimeType, media.data)
//                                )
//                            )
//                        }.toList())
//                        content = contentList
//                    }
                }

                return@map java.util.List.of<ChatCompletionMessage>(
                    ChatCompletionMessage(
                        content,
                        Role.valueOf(message.messageType.name)
                    )
                )
            }
        }.flatMap { obj: List<ChatCompletionMessage> -> obj.stream() }.toList()
        var request = ChatCompletionRequest(chatCompletionMessages, stream)
//        val enabledToolsToUse: MutableSet<String> = HashSet<Any?>()
//        if (prompt.options != null) {
//            val updatedRuntimeOptions = ModelOptionsUtils.copyToTarget(
//                prompt.options,
//                ChatOptions::class.java,
//                OpenAiChatOptions::class.java
//            ) as OpenAiChatOptions
//            enabledToolsToUse.addAll(this.runtimeFunctionCallbackConfigurations(updatedRuntimeOptions))
//            request = ModelOptionsUtils.merge(
//                updatedRuntimeOptions, request,
//                ChatCompletionRequest::class.java
//            ) as ChatCompletionRequest
//        }
//
//        if (!CollectionUtils.isEmpty(defaultOptions.getFunctions())) {
//            enabledToolsToUse.addAll(defaultOptions.getFunctions())
//        }

        request = ModelOptionsUtils.merge(
            request, this.defaultOptions,
            ChatCompletionRequest::class.java
        ) as ChatCompletionRequest
//        if (!CollectionUtils.isEmpty(enabledToolsToUse)) {
//            request = ModelOptionsUtils.merge(
//                OpenAiChatOptions.builder().withTools(this.getFunctionTools(enabledToolsToUse)).build(), request,
//                ChatCompletionRequest::class.java
//            ) as ChatCompletionRequest
//        }

        if (request.streamOptions() != null && !stream) {
            log.warn { "Removing streamOptions from the request as it is not a streaming request!" }
            request = request.withStreamOptions(null as StreamOptions?)
        }

        return request
    }
}
