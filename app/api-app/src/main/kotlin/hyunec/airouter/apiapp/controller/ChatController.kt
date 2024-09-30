package hyunec.airouter.apiapp.controller

import hyunec.airouter.chatdomain.dto.OpenAiChatRequest
import hyunec.airouter.chatdomain.port.OpenAiChatPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import reactor.core.CorePublisher

@RestController
class ChatController(
    @Qualifier("openAiChatWebClientAdapter") private val webclientChat: OpenAiChatPort,
    @Qualifier("openAiChatSpringAiAdapter") private val springAiChat: OpenAiChatPort,
) {
    @PostMapping("/api/chat/completions")
    fun chat(
        @RequestHeader mode: Mode = Mode.SPRING_AI,
        @RequestBody request: OpenAiChatRequest
    ): ResponseEntity<out CorePublisher<out Any>> {
        if (request.stream) {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(
                    when (mode) {
                        Mode.WEBCLIENT -> webclientChat.streamChat(request)
                        Mode.SPRING_AI -> springAiChat.streamChat(request)
                    }
                )
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                when (mode) {
                    Mode.WEBCLIENT -> webclientChat.chat(request)
                    Mode.SPRING_AI -> springAiChat.chat(request)
                }
            )
    }

    enum class Mode {
        WEBCLIENT, SPRING_AI
    }
}
