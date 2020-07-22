package ru.itmo.idu.admin.controllers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.idu.admin.api_classes.BaseResponse
import ru.itmo.idu.admin.api_classes.LoginRequest
import ru.itmo.idu.admin.api_classes.LoginResponse
import ru.itmo.idu.admin.api_classes.ResponseCodes
import ru.itmo.idu.admin.api_classes.dto.UserDTO
import ru.itmo.idu.admin.services.UserService
import ru.itmo.idu.admin.services.jwt.JwtProvider
import javax.validation.Valid

private val log = LoggerFactory.getLogger(LoginController::class.java)

@RestController
@RequestMapping("/api/v1/auth")
class LoginController(
        @Autowired
        val authenticationManager: AuthenticationManager,

        @Autowired
        val encoder: PasswordEncoder,

        @Autowired
        val jwtProvider: JwtProvider,

        @Autowired
        val userService: UserService
) {


    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): BaseResponse {
        log.info("Logging in user {}", loginRequest.email)
        val user = userService.checkLogin(loginRequest)

        return if (user != null) {
            val authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password))
            SecurityContextHolder.getContext().setAuthentication(authentication)
            val jwt: String = jwtProvider.generateJwtToken(user.email)
            LoginResponse(ResponseCodes.OK, UserDTO.fromUser(user), jwt)
        } else {
            log.warn("Failed to log in user {}", loginRequest.email)
            BaseResponse(ResponseCodes.INVALID_CREDENTIALS)
        }
    }

}
