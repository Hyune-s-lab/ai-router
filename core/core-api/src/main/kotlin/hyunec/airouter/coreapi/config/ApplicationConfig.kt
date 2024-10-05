package hyunec.airouter.coreapi.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["hyunec.airouter.coredomain"])
@ConfigurationPropertiesScan(basePackages = ["hyunec.airouter.coredomain.config"])
class ApplicationConfig
