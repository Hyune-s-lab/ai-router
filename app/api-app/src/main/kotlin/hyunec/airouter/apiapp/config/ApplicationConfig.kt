package hyunec.airouter.apiapp.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["hyunec.airouter.chatdomain"])
@ConfigurationPropertiesScan(basePackages = ["hyunec.airouter.chatdomain.config"])
class ApplicationConfig
