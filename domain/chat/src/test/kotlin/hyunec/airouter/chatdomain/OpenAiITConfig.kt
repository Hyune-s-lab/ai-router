package hyunec.airouter.chatdomain

import hyunec.airouter.chatdomain.config.OpenAiConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class OpenAiITConfig {

    private val log = KotlinLogging.logger {}

    @Bean
    fun openAiConfig(): OpenAiConfig {
        return OpenAiConfig(
            baseUrl = "https://api.openai.com/",
            apiKey = "fill me real key"
        )
    }
}
