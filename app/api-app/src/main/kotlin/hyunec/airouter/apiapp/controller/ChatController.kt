package hyunec.airouter.apiapp.controller

import hyunec.airouter.chatdomain.dto.OpenAiChatRequest
import hyunec.airouter.chatdomain.port.OpenAiChatPort
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ChatController(
    private val openAiChatPort: OpenAiChatPort
) {
    @PostMapping("/api/chat/completions")
    fun chat(@RequestBody request: OpenAiChatRequest): ResponseEntity<out Flux<out String>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(openAiChatPort.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(openAiChatPort.chat(request).flux())
    }
}
