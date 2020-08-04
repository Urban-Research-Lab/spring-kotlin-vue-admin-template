package ru.itmo.idu.admin.integration

import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import ru.itmo.idu.admin.config.SecurityConfig
import ru.itmo.idu.admin.config.WebServiceConfiguration

@RunWith(SpringJUnit4ClassRunner::class)
@WebAppConfiguration
@DataJpaTest
@AutoConfigurationPackage
@ContextConfiguration(classes = [WebServiceConfiguration::class, SecurityConfig::class, TestConfig::class])
@ActiveProfiles("test")
abstract class BaseIntegrationTest

@Configuration
@ComponentScan(basePackages = ["ru.itmo.idu.admin"])
internal class TestConfig// I have no idea why do I have to specify it explicitly