package hyunec.airouter.chatdomain.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ai-provider.openai")
class OpenAiConfig(
    val baseUrl: String,
    val apiKey: String,
)
