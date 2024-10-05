package hyunec.airouter.coredomain.chat

import hyunec.airouter.coredomain.TestDefaultSupport
import hyunec.airouter.coredomain.chat.dto.ChatRequest
import hyunec.airouter.coredomain.chat.dto.ChatRequest.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * application.yaml 에 api-key 를 넣어야 합니다.
 */
@Ignore
@SpringBootTest
class AnthropicChatIT(
    private val chatClientSut: AnthropicChatClientService,
    private val apiSut: AnthropicApiService,
    private val webClientSut: AnthropicWebClientService
) : TestDefaultSupport() {
    val log = KotlinLogging.logger {}

    /**
     * ChatResponse 를 반환하는 ChatClientService 를 테스트합니다.
     */
    @Nested
    inner class ChatClient {
        @Test
        fun `non_stream chat By ChatClient`() {
            val request = createRequest()
            val response = chatClientSut.chat(request).block()

            log.info { "### response: $response" }

            response shouldNotBe null
        }

        @Test
        fun `stream chat By ChatClient`() {
            val request = createRequest(stream = true)
            StepVerifier.create(chatClientSut.streamChat(request))
                .expectSubscription()
                .thenConsumeWhile { log.info { "### $it" }; true }
                .verifyComplete()
        }
    }

    /**
     * ChatCompletion 을 반환하는 ApiService 를 테스트합니다.
     */
    @Nested
    inner class Api {
        @Test
        fun `non_stream chat By api`() {
            val request = createRequest()
            val response = apiSut.chat(request).block()

            log.info { "### response: $response" }

            response shouldNotBe null
        }

        @Test
        fun `stream chat By api`() {
            val request = createRequest(stream = true)
            StepVerifier.create(apiSut.streamChat(request))
                .expectSubscription()
                .thenConsumeWhile { log.info { "### $it" }; true }
                .verifyComplete()
        }
    }

    /**
     * String 을 반환하는 WebClientService 를 테스트합니다.
     */
    @Nested
    inner class WebClient {
        @Test
        fun `non_stream chat By WebClient`() {
            val request = createRequest()
            val response = webClientSut.chat(request).block()

            log.info { "### response: $response" }

            response shouldNotBe null
        }

        @Test
        fun `stream chat By WebClient`() {
            val request = createRequest(stream = true)
            StepVerifier.create(webClientSut.streamChat(request))
                .expectSubscription()
                .thenConsumeWhile { log.info { "### $it" }; true }
                .verifyComplete()
        }
    }

    companion object {
        fun createRequest(stream: Boolean = false) = ChatRequest(
            model = Model.CLAUDE_3_5_SONNET_20240620,
            messages = listOf(
                Message(
                    role = MessageType.USER,
                    content = "hello"
                )
            ),
            stream = stream
        )
    }
}
