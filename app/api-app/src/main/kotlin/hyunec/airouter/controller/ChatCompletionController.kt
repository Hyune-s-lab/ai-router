package hyunec.airouter.controller

import hyunec.airouter.service.ChatRequest
import hyunec.airouter.service.OpenAIService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ChatCompletionController(
    private val openAIService: OpenAIService
) {
    @PostMapping("/api/chat/completions")
    fun chatCompletion(@RequestBody request: ChatRequest): ResponseEntity<out Flux<out String>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(openAIService.streamChat(request))
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(openAIService.chat(request).flux())
    }
}
