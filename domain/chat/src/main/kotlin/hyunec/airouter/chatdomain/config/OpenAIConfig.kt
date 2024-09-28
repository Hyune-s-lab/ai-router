package hyunec.airouter.chatdomain.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ai-provider.openai")
class OpenAIConfig(
    val baseUrl: String,
    val apiKey: String,
)
