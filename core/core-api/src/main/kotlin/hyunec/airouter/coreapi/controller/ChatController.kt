package hyunec.airouter.coreapi.controller

import hyunec.airouter.coredomain.chat.ChatService
import hyunec.airouter.coredomain.chat.dto.ChatRequest
import hyunec.airouter.coredomain.common.AiProvider
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.CorePublisher

@RestController
class ChatController(
    private val openAiApiService: ChatService,
    private val anthropicApiService: ChatService
) {
    @PostMapping("/api/chat/completions")
    fun chat(
        @RequestBody request: ChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        when (request.model.provider) {
            AiProvider.OPENAI -> {
                if (request.stream) {
                    return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(openAiApiService.streamChat(request))
                }

                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(openAiApiService.chat(request))
            }

            AiProvider.ANTHROPIC -> {
                if (request.stream) {
                    return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(anthropicApiService.streamChat(request))
                }

                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(anthropicApiService.chat(request))
            }
        }
    }
}
