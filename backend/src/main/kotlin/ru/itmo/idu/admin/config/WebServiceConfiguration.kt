package ru.itmo.idu.admin.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.client.RestTemplate


@Configuration
@EnableJpaRepositories(basePackages = ["ru.itmo.idu.admin.repositories"])
@EnableTransactionManagement
@EnableConfigurationProperties
@ComponentScan("ru.itmo.idu.admin")
@EntityScan("ru.itmo.idu.admin.model.**")
@SpringBootApplication(scanBasePackages = ["ru.itmo.idu.admin"])
@EnableScheduling
@EnableAsync
class WebServiceConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper? {
        val om = ObjectMapper()
        om.registerModule(KotlinModule())
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return om
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun messageSource(): ResourceBundleMessageSource {
        val source = ResourceBundleMessageSource()
        source.setBasenames("messages")
        source.setUseCodeAsDefaultMessage(true)
        source.setDefaultEncoding("UTF-8")
        return source
    }

    @Bean
    fun customOpenAPI(): OpenAPI? {
        return OpenAPI()
            .components(
                Components().addSecuritySchemes(
                    "AuthToken",
                    SecurityScheme().type(SecurityScheme.Type.APIKEY).`in`(SecurityScheme.In.HEADER).name("Authorization")
                )
            )
            .info(Info().title("Admin Template API").version("1.0.0"))
    }
}