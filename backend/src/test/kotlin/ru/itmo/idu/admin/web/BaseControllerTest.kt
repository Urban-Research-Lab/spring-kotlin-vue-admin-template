package ru.itmo.idu.admin.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.itmo.idu.admin.api_classes.BaseResponse
import ru.itmo.idu.admin.config.SecurityConfig
import ru.itmo.idu.admin.config.WebServiceConfiguration
import ru.itmo.idu.admin.integration.TestConfig

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [WebServiceConfiguration::class, SecurityConfig::class, TestConfig::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    protected fun <T: BaseResponse> makeAPIPOST(path: String, body: Any?, responseClass: Class<T>): T {
        val requestBuilder = post(path)
        if (body != null) {
            requestBuilder.contentType("application/json").content(objectMapper.writeValueAsBytes(body))
        }
        return makeAPIRequest(requestBuilder, responseClass)

    }

    protected fun <T: BaseResponse> makeAPIRequest(requestBuilder: RequestBuilder, responseClass: Class<T>): T {
        val responseString = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().response.contentAsByteArray
        return objectMapper.readValue(responseString, responseClass)
    }
}