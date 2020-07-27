package ru.itmo.idu.admin.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.client.RestTemplate


@Configuration
@EnableJpaRepositories(basePackages = ["ru.itmo.idu.admin.repositories"])
@EnableTransactionManagement
@EnableConfigurationProperties
@EntityScan("ru.itmo.idu.admin.model")
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
}