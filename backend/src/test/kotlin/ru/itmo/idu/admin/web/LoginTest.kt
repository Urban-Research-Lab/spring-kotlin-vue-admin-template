package ru.itmo.idu.admin.web

import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import ru.itmo.idu.admin.api_classes.LoginRequest
import ru.itmo.idu.admin.api_classes.LoginResponse
import ru.itmo.idu.admin.api_classes.ResponseCodes
import ru.itmo.idu.admin.api_classes.UserRegistrationRequest
import ru.itmo.idu.admin.services.UserService

class LoginTest: BaseControllerTest() {

    @Autowired
    lateinit var userService: UserService

    @Test
    fun checkCorrectLogin() {

        val response = makeAPIPOST("/api/v1/auth/login", LoginRequest("1@example.com", "12345"), LoginResponse::class.java)

        Assert.assertEquals(0, response.code)
        Assert.assertNotNull(response.user)
        Assert.assertEquals("1@example.com", response.user?.email)
        Assert.assertNotNull(response.token)
    }

    @Test
    fun checkIncorrectLogin() {
        val response = makeAPIPOST("/api/v1/auth/login", LoginRequest("1@example.com", "invalidpass"), LoginResponse::class.java)
        Assert.assertEquals(ResponseCodes.INVALID_CREDENTIALS.code, response.code)
        Assert.assertNull(response.token)
        Assert.assertNull(response.user)
    }

    @Test
    @WithMockUser("1@example.com")
    fun checkLoginBannedUser() {
        val user = userService.registerUser(UserRegistrationRequest("test", "test@example.com", "12345", mutableListOf()))

        userService.banUser(user.id, "Test Ban")

        val response = makeAPIPOST("/api/v1/auth/login", LoginRequest("test@example.com", "12345"), LoginResponse::class.java)
        Assert.assertEquals(ResponseCodes.ACCOUNT_BANNED.code, response.code)

        Assert.assertNotNull(response.banInfo)
        Assert.assertEquals("Test Ban", response.banInfo?.reason)
    }


}