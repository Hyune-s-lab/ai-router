package hyunec.airouter.chatdomain.port

import hyunec.airouter.chatdomain.dto.OpenAiChatRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OpenAiChatPort {
    fun chat(request: OpenAiChatRequest): Mono<out Any>

    fun streamChat(request: OpenAiChatRequest): Flux<out Any>
}
