package hyunec.airouter.coredomain.chat

import hyunec.airouter.coredomain.chat.dto.ChatRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.anthropic.AnthropicChatModel
import org.springframework.ai.anthropic.AnthropicChatOptions
import org.springframework.ai.anthropic.api.AnthropicApi
import org.springframework.ai.anthropic.api.AnthropicApi.*
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.messages.ToolResponseMessage
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.ModelOptionsUtils
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Service
class AnthropicChatClientService(
    anthropicApi: AnthropicApi
) : ChatService, AnthropicChatModel(anthropicApi) {
    private final val log = KotlinLogging.logger {}

    override fun chat(request: ChatRequest): Mono<out Any> {
        log.debug { "### springAi chat" }

        val chatCompletionRequest = createRequest(request.toPrompt(), false)

        return anthropicApi.chatCompletionEntity(chatCompletionRequest).run {
            Mono.just(this.body!!)
        }
    }

    override fun streamChat(request: ChatRequest): Flux<out Any> {
        log.debug { "### springAi streamChat" }

        val chatCompletionRequest = createRequest(request.toPrompt(), true)

        return anthropicApi.chatCompletionStream(chatCompletionRequest)
            .map { it as Any }
    }

    /**
     * spring-ai 에 package-private 로 선언되어 있는 로직 입니다.
     * - image, function call 로직은 disable 했습니다.
     */
    fun createRequest(prompt: Prompt, stream: Boolean): ChatCompletionRequest {
        val functionsForThisRequest: MutableSet<String?> = HashSet()

        val userMessages = prompt.instructions.stream()
            .filter { message -> message.messageType != MessageType.SYSTEM }
            .map { message ->
                when (message.messageType) {
                    MessageType.USER -> {
                        val contents: MutableList<ContentBlock> = ArrayList(listOf(ContentBlock(message.content)))
//                        if (message is UserMessage) {
//                            if (!CollectionUtils.isEmpty(message.media)) {
//                                val mediaContent = message.media
//                                    .stream()
//                                    .map { media: Media ->
//                                        ContentBlock(
//                                            media.mimeType.toString(),
//                                            this.fromMediaData(media.data)
//                                        )
//                                    }
//                                    .toList()
//                                contents.addAll(mediaContent)
//                            }
//                        }
                        return@map AnthropicMessage(contents, Role.valueOf(message.messageType.name))
                    }

                    MessageType.ASSISTANT -> {
                        val assistantMessage = message as AssistantMessage
                        val contentBlocks: MutableList<ContentBlock> = ArrayList()
                        if (StringUtils.hasText(message.getContent())) {
                            contentBlocks.add(ContentBlock(message.getContent()))
                        }
                        if (!CollectionUtils.isEmpty(assistantMessage.toolCalls)) {
                            for (toolCall in assistantMessage.toolCalls) {
                                contentBlocks.add(
                                    ContentBlock(
                                        ContentBlock.Type.TOOL_USE, toolCall.id(), toolCall.name(),
                                        ModelOptionsUtils.jsonToMap(toolCall.arguments())
                                    )
                                )
                            }
                        }
                        return@map AnthropicMessage(contentBlocks, Role.ASSISTANT)
                    }

                    MessageType.TOOL -> {
                        val toolResponses = (message as ToolResponseMessage).responses
                            .stream()
                            .map { toolResponse ->
                                ContentBlock(
                                    ContentBlock.Type.TOOL_RESULT, toolResponse.id(),
                                    toolResponse.responseData()
                                )
                            }
                            .toList()
                        return@map AnthropicMessage(toolResponses, Role.USER)
                    }

                    else -> throw IllegalArgumentException("Unsupported message type: " + message.messageType)
                }
            }
            .toList()

        val systemPrompt = prompt.instructions
            .stream()
            .filter { m -> m.messageType == MessageType.SYSTEM }
            .map { m -> m.content }
            .collect(Collectors.joining(System.lineSeparator()))

        var request = ChatCompletionRequest(
            defaultOptions.model, userMessages,
            systemPrompt, defaultOptions.maxTokens, defaultOptions.temperature, stream
        )

        if (prompt.options != null) {
            val updatedRuntimeOptions = ModelOptionsUtils.copyToTarget(
                prompt.options,
                ChatOptions::class.java, AnthropicChatOptions::class.java
            )

            functionsForThisRequest.addAll(this.runtimeFunctionCallbackConfigurations(updatedRuntimeOptions))

            request = ModelOptionsUtils.merge(updatedRuntimeOptions, request, ChatCompletionRequest::class.java)
        }

//        if (!CollectionUtils.isEmpty(defaultOptions.getFunctions())) {
//            functionsForThisRequest.addAll(defaultOptions.getFunctions())
//        }

        request = ModelOptionsUtils.merge(
            request, this.defaultOptions,
            ChatCompletionRequest::class.java
        )

//        if (!CollectionUtils.isEmpty(functionsForThisRequest)) {
//            val tools = getFunctionTools(functionsForThisRequest)
//
//            request = AnthropicApi.ChatCompletionRequest.from(request).withTools(tools).build()
//        }

        return request
    }
}
