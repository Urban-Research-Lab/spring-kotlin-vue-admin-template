package ru.itmo.idu.admin.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

private val log = LoggerFactory.getLogger(EmailService::class.java)

@Service
class EmailService(
        @Autowired
        val emailSender: JavaMailSender,

        @Value("\${mail.enabled:false}")
        val enabled: Boolean
) {
    fun sendSimpleMessage(to: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject(subject)
        message.setFrom("info@yourmaps.io")
        message.setText(text)
        log.info("Sending simple message: {}", message)
        if (enabled) {
            emailSender.send(message)
        } else {
            log.info("Email disabled in config")
        }
    }
}