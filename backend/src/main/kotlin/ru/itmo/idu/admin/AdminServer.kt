package ru.itmo.idu.admin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import ru.itmo.idu.admin.config.WebServiceConfiguration

val log: Logger = LoggerFactory.getLogger("main")

object Dev {
    @JvmStatic
    fun main(args: Array<String>) {
        log.info("Running in DEV mode")
        System.setProperty("spring.config.location", "./backend/src/main/resources/application.yml," +
                "./backend/src/main/resources/local.application.yml")
        AdminServer.main(args)
    }
}

object Prod {
    @JvmStatic
    fun main(args: Array<String>) {
        log.info("Running in PROD mode")
        System.setProperty("spring.config.location", "classpath:/application.yml," +
                "/etc/adminservice/application.yml")
        AdminServer.main(args)
    }
}

object AdminServer {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            log.info("Starting Admin Server")
            val applicationBuilder = SpringApplicationBuilder(WebServiceConfiguration::class.java)
            applicationBuilder.profiles("default")
            applicationBuilder.bannerMode(Banner.Mode.OFF)
            val application = applicationBuilder.build()
            application.run(*args)
        } catch (e: Exception) {
            log.error("Error starting application", e)
        }

    }
}