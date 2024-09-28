package hyunec.airouter.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class ChatController() {

    @PostMapping("/api/chat/completions")
    fun chatCompletion(@RequestBody request: ChatCompletion.Request): ResponseEntity<Any> {
        return if (request.stream) {
            ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(
                    Flux.interval(Duration.ofMillis(100))
                        .take(5)
                        .map {
                            """{"id":"chatcmpl-ACSoUZd7DWc08oRwdvzuTVjSMbqRY","object":"chat.completion.chunk","created":${System.currentTimeMillis()},"model":"gpt-4o-2024-05-13","system_fingerprint":"fp_057232b607","choices":[{"index":0,"delta":{"role":"assistant","content":"","refusal":null},"logprobs":null,"finish_reason":null}]}"""
                        }
                )
        } else {
            ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(mapOf("message" to "non-stream")))
        }
    }

    class ChatCompletion {
        data class Request(
            val model: String,
            val messages: List<Message>,
            val stream: Boolean = false
        )

        data class Message(
            val role: String,
            val content: String
        )
    }
}
