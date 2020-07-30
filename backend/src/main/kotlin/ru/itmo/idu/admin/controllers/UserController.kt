package ru.itmo.idu.admin.controllers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.idu.admin.api_classes.*
import ru.itmo.idu.admin.api_classes.dto.UserDTO
import ru.itmo.idu.admin.services.UserService

private val log = LoggerFactory.getLogger(UserController::class.java)

@RestController
@RequestMapping("/api/v1/user")
class UserController(
        @Autowired
        val userService: UserService
) {

    @PostMapping("new")
    fun registerUser(
            @RequestBody
            request: UserRegistrationRequest
    ): ObjectResponse {
        log.info("registerUser(request = {})", request)
        val user = userService.registerUser(request);
        return ObjectResponse(UserDTO.fromUser(user))
    }

    @PostMapping("/{id}")
    fun updateUser(
            @PathVariable("id") id: Long,
            @RequestBody request: UserUpdateRequest
    ): ObjectResponse {
        log.info("updateUser(id = {}, request = {})", id, request)
        val updated = userService.updateUser(id, request)
        return ObjectResponse(UserDTO.fromUser(updated))
    }

    @GetMapping("/{id}")
    fun getUser(
       @PathVariable("id") id: Long
    ): ObjectResponse {
        log.info("getUser(id = {})", id)
        val user = userService.getUser(id)
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