package hyunec.airouter.coreapi.controller

import hyunec.airouter.coredomain.chat.ChatService
import hyunec.airouter.coredomain.chat.dto.ChatRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.CorePublisher

/**
 * ai 구현체 별로 response dto 가 다른 것을 확인하는 api
 */
@RestController
class AnthropicChatController(
    private val anthropicApiService: ChatService,
    private val anthropicChatClientService: ChatService,
    private val anthropicWebClientService: ChatService,
) {
    @PostMapping("/api/chat/completions/spring-api/anthropic")
    fun chat(
        @RequestBody request: ChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(anthropicApiService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(anthropicApiService.chat(request))
    }

    @PostMapping("/api/chat/completions/chat-client/anthropic")
    fun chatClient(
        @RequestBody request: ChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(anthropicChatClientService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(anthropicChatClientService.chat(request))
    }

    @PostMapping("/api/chat/completions/webclient/anthropic")
    fun chatWebClient(
        @RequestBody request: ChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(anthropicWebClientService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(anthropicWebClientService.chat(request))
    }
}
