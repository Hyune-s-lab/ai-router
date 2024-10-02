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
class ChatController2(
    private val openAiApiService: ChatService,
    private val openAiChatClientService: ChatService,
    private val openAiWebClientService: ChatService,
) {
    @PostMapping("/api/chat/completions/spring-api")
    fun chat(
        @RequestBody request: ChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(openAiApiService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(openAiApiService.chat(request))
    }

    @PostMapping("/api/chat/completions/chat-client")
    fun chatClient(
        @RequestBody request: ChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(openAiChatClientService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(openAiChatClientService.chat(request))
    }

    @PostMapping("/api/chat/completions/webclient")
    fun chatWebClient(
        @RequestBody request: ChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(openAiWebClientService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(openAiWebClientService.chat(request))
    }
}
