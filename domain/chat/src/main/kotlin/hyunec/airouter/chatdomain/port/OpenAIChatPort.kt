package hyunec.airouter.chatdomain.port

import hyunec.airouter.chatdomain.dto.OpenAIChatRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OpenAIChatPort {
    fun chat(request: OpenAIChatRequest): Mono<String>

    fun streamChat(request: OpenAIChatRequest): Flux<String>
}
