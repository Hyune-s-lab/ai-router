package hyunec.airouter.coredomain

import hyunec.airouter.coredomain.dto.OpenAiChatRequest
import hyunec.airouter.coredomain.port.OpenAiChatSpringAiAdapter
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.shouldNotBe
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
@Import(OpenAiITConfig::class)
class OpenAiChatSpringAiAdapterIT(
    private val sut: OpenAiChatSpringAiAdapter
) : TestDefaultSupport() {

    val log = KotlinLogging.logger {}

    @Test
    fun `chat test - non_stream`() {
        val response = sut.chat(request).block()

        log.info { "### response: $response" }

        response shouldNotBe null
    }

    @Test
    fun `chat test - stream`() {
        StepVerifier.create(sut.streamChat(request))
            .expectSubscription()
            .thenConsumeWhile { log.info { "### $it" }; true }
            .verifyComplete()
    }

    companion object {
        val request = OpenAiChatRequest(
            model = "gpt-4o",
            messages = listOf(
                OpenAiChatRequest.Message(
                    role = "system",
                    content = "You are a helpful assistant."
                ),
                OpenAiChatRequest.Message(
                    role = "user",
                    content = "hello world"
                )
            )
        )
    }
}
