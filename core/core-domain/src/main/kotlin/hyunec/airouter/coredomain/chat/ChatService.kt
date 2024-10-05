package hyunec.airouter.coredomain.chat

import hyunec.airouter.coredomain.chat.dto.ChatRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatService {
    fun chat(request: ChatRequest): Mono<out Any>

    fun streamChat(request: ChatRequest): Flux<out Any>
}
