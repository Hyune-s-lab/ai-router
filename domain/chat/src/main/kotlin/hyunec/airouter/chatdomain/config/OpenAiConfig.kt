package hyunec.airouter.chatdomain.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAiConfig(
    @Value("\${ai-provider.openai.base-url}") val baseUrl: String,
    @Value("\${ai-provider.openai.api-key}") val apiKey: String,
) {
    final val log = KotlinLogging.logger {}

    init {
        log.debug { "----- OpenAiConfig init" }
        log.debug { "ai-provider.openai.base-url: $baseUrl" }
        log.debug { "ai-provider.openai.api-key : ${if (apiKey.length > 20) apiKey.substring(20) else apiKey}" }

        assert(baseUrl.isNotBlank()) { "ai-provider.openai.base-url must not be blank" }
        assert(apiKey.startsWith("sk")) { "ai-provider.openai.api-key must start with 'sk'" }

        log.debug { "----- OpenAiConfig init end" }
    }
}
