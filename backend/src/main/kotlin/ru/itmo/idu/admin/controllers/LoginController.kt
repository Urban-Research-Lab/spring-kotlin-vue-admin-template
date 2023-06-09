package ru.itmo.idu.admin.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.idu.admin.api_classes.*
import ru.itmo.idu.admin.api_classes.dto.UserDTO
import ru.itmo.idu.admin.model.User
import ru.itmo.idu.admin.model.UserStatus
import ru.itmo.idu.admin.services.UserService
import ru.itmo.idu.admin.services.jwt.JwtProvider

private val log = LoggerFactory.getLogger(LoginController::class.java)

@RestController
@RequestMapping("/api/v1/auth")
class LoginController(
        @Autowired
        val jwtProvider: JwtProvider,

        @Autowired
        val userService: UserService
) {

    private fun createResponseForUser(user: User?, loginValue: String): LoginResponse {
        return if (user != null) {

            if (user.status == UserStatus.BANNED) {
                log.warn("User {} is banned", user.email)
                return LoginResponse(ResponseCodes.ACCOUNT_BANNED.code,
                        ResponseCodes.ACCOUNT_BANNED.message,
                        null,
                        null,
                        BanInfoDTO(user.banInfo!!.id, user.banInfo!!.reason)
                )
            }
            val jwt: String = jwtProvider.generateJwtToken(user.email)
            LoginResponse(ResponseCodes.OK, UserDTO.fromUser(user), jwt)
        } else {
            log.warn("Failed to log in user {}", loginValue)
            LoginResponse(ResponseCodes.INVALID_CREDENTIALS)
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user if it exists in a database",
        description = "Authenticate user if given login and password are correct and exist in a database.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User was found"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "404", description = "User wasn't found")]
    )
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): BaseResponse {
        log.info("Logging in user {}", loginRequest.email)
        val user = userService.checkLogin(loginRequest)
        return createResponseForUser(user, loginRequest.email)
    }

}
