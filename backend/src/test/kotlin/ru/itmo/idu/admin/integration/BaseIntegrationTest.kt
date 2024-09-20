package ru.itmo.idu.admin.integration

import org.junit.Assert
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import ru.itmo.idu.admin.api_classes.UserRegistrationRequest
import ru.itmo.idu.admin.config.SecurityConfig
import ru.itmo.idu.admin.config.WebServiceConfiguration
import ru.itmo.idu.admin.model.User
import ru.itmo.idu.admin.model.UserStatus
import ru.itmo.idu.admin.services.SecurityService
import ru.itmo.idu.admin.services.UserService

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [WebServiceConfiguration::class, SecurityConfig::class, TestConfig::class])
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var securityService: SecurityService

    protected fun createTestUser(email: String, pass: String): User {
        val user = userService.registerUser(UserRegistrationRequest(
                email,
                email,
                pass,
                listOf()
        ))
        user.status = UserStatus.ACTIVE
        userService.saveUser(user)
        return user
    }

    protected fun logOutCurrentUser() {
        SecurityContextHolder.getContext().authentication = AnonymousAuthenticationToken("123", "anonymousUser", listOf(SimpleGrantedAuthority("anonymous")))
        Assert.assertNull(securityService.getCurrentUser())
    }

    protected fun loginUser(email: String, pass: String) {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(org.springframework.security.core.userdetails.User(
                email,
                pass,
                mutableListOf()
        ), pass)
        Assert.assertNotNull(securityService.getCurrentUser())
    }

}

@Configuration
@ComponentScan(basePackages = ["ru.itmo.idu.admin"])
internal class TestConfig {
    // had to mock oauth2 classes as for whatever reason they are not created in test context
    @Bean
    fun oauth2AuthorizedClientService(): OAuth2AuthorizedClientService {
        return Mockito.mock(OAuth2AuthorizedClientService::class.java)
    }

    @Bean
    fun clientRegistrationRepository(): ClientRegistrationRepository {
        return Mockito.mock(ClientRegistrationRepository::class.java)
    }

    @Bean
    @Primary
    fun mockMailSender(): JavaMailSender {
        return Mockito.mock(JavaMailSender::class.java)
    }

    @Bean
    @Primary
    fun taskExecutor(): TaskExecutor {
        return SyncTaskExecutor()
    }

    @Bean
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun mockRestTemplate(): RestTemplate {
        val mock = Mockito.mock(RestTemplate::class.java)
        // set up initial mock to prevent exceptions in log, as this method is called each time we save a route
        `when`(mock.getForObject(ArgumentMatchers.anyString(),
            ArgumentMatchers.eq(String::class.java),
            ArgumentMatchers.anyString())).thenReturn("{ \"results\": []}")
        return mock
    }
}