package hyunec.airouter.apiapp.controller

import hyunec.airouter.chatdomain.dto.OpenAiChatRequest
import hyunec.airouter.chatdomain.port.OpenAiChatPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.CorePublisher

@RestController
class ChatController(
    @Qualifier("openAiChatWebClientAdapter") private val webclientChat: OpenAiChatPort,
    @Qualifier("openAiChatSpringAiAdapter") private val springAiChat: OpenAiChatPort,
) {
    private var mode = "webclient"

    @PostMapping("/api/chat/completions")
    fun chat(@RequestBody request: OpenAiChatRequest): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(
                    when (mode) {
                        "webclient" -> webclientChat.streamChat(request)
                        "spring-ai" -> springAiChat.streamChat(request)
                        else -> throw IllegalArgumentException("Invalid mode: $mode")
                    }
                )
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                when (mode) {
                    "webclient" -> webclientChat.chat(request)
                    "spring-ai" -> springAiChat.chat(request)
                    else -> throw IllegalArgumentException("Invalid mode: $mode")
                }
            )
    }

    @PostMapping("/api/chat/mode")
    fun setMode(@RequestBody mode: String): String {
        this.mode = mode
        return this.mode
    }

    @GetMapping("/api/chat/mode")
    fun getMode(): String {
        return this.mode
    }
}
