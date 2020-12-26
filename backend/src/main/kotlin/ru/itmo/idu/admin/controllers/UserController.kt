package ru.itmo.idu.admin.controllers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.idu.admin.api_classes.*
import ru.itmo.idu.admin.api_classes.dto.UserDTO
import ru.itmo.idu.admin.services.SecurityService
import ru.itmo.idu.admin.services.UserService

private val log = LoggerFactory.getLogger(UserController::class.java)

@RestController
@RequestMapping("/api/v1/user")
class UserController(
        @Autowired
        val userService: UserService,

        @Autowired
        val securityService: SecurityService
) {

    @PostMapping("new")
    fun registerUser(
            @RequestBody
            request: UserRegistrationRequest
    ): ObjectResponse<UserDTO> {
        log.info("registerUser(request = {})", request)
        val user = userService.registerUser(request);
        return ObjectResponse(UserDTO.fromUser(user))
    }

    @PostMapping("activate")
    fun activateUser(
            @RequestParam token: String
    ): StringResponse {
        log.info("activateUser(token = {})", token)
        val email = userService.activateUser(token)
        return StringResponse(email)
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/admin/activate")
    fun activateUserAdmin(@RequestParam email: String): BaseResponse {
        log.info("activateUserAdmin(email = {})", email)
        userService.activateUserAdmin(email)
        return BaseResponse(ResponseCodes.OK)
    }

    @PreAuthorize("hasAuthority('BAN_USERS')")
    @PostMapping("/{id}/ban")
    fun banUser(
            @PathVariable("id") id: Long,
            @RequestParam reason: String
    ): BaseResponse {
        log.info("banUser(id = {}, reason = {})", id, reason)
        userService.banUser(id, reason)
        return BaseResponse(ResponseCodes.OK)
    }

    @PreAuthorize("hasAuthority('BAN_USERS')")
    @PostMapping("/{id}/unban")
    fun unbanUser(
            @PathVariable("id") id: Long
    ): BaseResponse {
        log.info("unbanUser(id = {}, reason = {})", id)
        userService.unbanUser(id)
        return BaseResponse(ResponseCodes.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}")
    fun updateUser(
            @PathVariable("id") id: Long,
            @RequestBody request: UserUpdateRequest
    ): ObjectResponse<UserDTO> {
        log.info("updateUser(id = {}, request = {})", id, request)
        val updated = userService.updateUser(id, request)
        return ObjectResponse(UserDTO.fromUser(updated))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    fun deleteUser(@PathVariable("id") id: Long): BaseResponse {
        log.info("deleteUser(id = {})", id)
        userService.deleteUser(id)
        return BaseResponse(ResponseCodes.OK)
    }

    @PostMapping("/createPasswordRestoreToken")
    fun createPasswordRestoreToken(@RequestParam email: String): BaseResponse {
        log.info("createPasswordRestoreToken(email = {})", email)
        userService.createPasswordRestoreToken(email)
        return BaseResponse(ResponseCodes.OK)
    }

    @PostMapping("/restorePassword")
    fun restorePassword(@RequestParam token: String, @RequestParam newPassword: String): BaseResponse {
        log.info("restorePassword(token = {}, newPassword = ****)", token)
        userService.restorePassword(token, newPassword)
        return BaseResponse(ResponseCodes.OK)
    }

    @GetMapping("/{id}")
    fun getUser(
       @PathVariable("id") id: Long
    ): ObjectResponse<UserDTO> {
        log.info("getUser(id = {})", id)
        val user = userService.getUser(id)
        return ObjectResponse(UserDTO.fromUser(user))
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    fun getCurrentUser(): ObjectResponse<UserDTO> {
        log.info("getCurrentUser()")
        val user = securityService.getCurrentUser() ?: return ObjectResponse(ResponseCodes.INVALID_CREDENTIALS)
        return ObjectResponse(UserDTO.fromUser(user))
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/count")
    fun countUsers(): NumberResponse {
        log.info("countUsers()")
        val count = userService.countUsers()
        return NumberResponse(count)
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/list")
    fun listUsers(
            @RequestParam(required = false, defaultValue = "0") page: Int,
            @RequestParam(required = false, defaultValue = "10") size: Int

    ): ObjectListResponse {
        log.info("listUsers(page = {}, size = {})", page, size)
        return ObjectListResponse(userService.getUsers(page, size).map { UserDTO.fromUser(it) })
    }
}