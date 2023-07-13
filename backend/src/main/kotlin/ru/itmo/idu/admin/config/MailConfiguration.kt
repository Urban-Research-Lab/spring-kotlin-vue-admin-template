package ru.itmo.idu.admin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*


@Configuration
class MailConfiguration {
    @get:Bean
    val javaMailSender: JavaMailSender
        get() {
            val mailSender = JavaMailSenderImpl()
            mailSender.host = "smtp.gmail.com"
            mailSender.port = 587
            mailSender.username = "login"
            mailSender.password = "password"
            val props: Properties = mailSender.javaMailProperties
            props.put("mail.transport.protocol", "smtp")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.starttls.enable", "true")
            return mailSender
        }
}