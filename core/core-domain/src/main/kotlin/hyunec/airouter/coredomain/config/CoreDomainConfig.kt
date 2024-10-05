package hyunec.airouter.coredomain.config

import hyunec.airouter.coredomain.common.AiProperty
import hyunec.airouter.coredomain.common.AiProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ConfigurationProperties(prefix = "ai")
class CoreDomainConfig {
    private final val log = KotlinLogging.logger {}

    lateinit var properties: List<AiProperty>

    @PostConstruct
    fun postConstruct() {
        log.debug { "--- AiPropertyConfig check start" }

        properties.forEachIndexed() { index, property ->
            log.debug { "property[$index]: $property" }
        }

        log.debug { "--- AiPropertyConfig check end" }
    }

    @Bean
    fun openAiApi(): OpenAiApi {
        return properties.first { it.provider == AiProvider.OPENAI }.run {
            OpenAiApi(apiKey)
        }
    }

    @Bean
    fun openAiWebClient(): WebClient {
        return properties.first { it.provider == AiProvider.OPENAI }.run {
            WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders {
                    it[HttpHeaders.AUTHORIZATION] = "Bearer $apiKey"
                    it[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
                }
                .build()
        }
    }
}
