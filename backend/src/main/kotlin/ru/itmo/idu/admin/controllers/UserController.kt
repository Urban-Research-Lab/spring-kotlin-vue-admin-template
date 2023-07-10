package ru.itmo.idu.admin.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @Operation(summary = "Add a new user",
        description = "Add a new user to include UserDTO parameters into a database")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User was created", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun registerUser(
            @RequestBody
            request: UserRegistrationRequest
    ): ObjectResponse<UserDTO> {
        log.info("registerUser(request = {})", request)
        val user = userService.registerUser(request)
        return ObjectResponse(UserDTO.fromUser(user))
    }

    @PostMapping("activate")
    @Operation(summary = "Activate user",
        description = "Activate user by the given token and searching matches in database")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User was activated", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "User wasn't found", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun activateUser(
            @RequestParam token: String
    ): StringResponse {
        log.info("activateUser(token = {})", token)
        val email = userService.activateUser(token)
        return StringResponse(email)
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/admin/activate")
    @Operation(summary = "Activate admin",
        description = "Activate admin by the given email with checking if the user has been activated and with searching matches in database")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Admin was activated", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "There is no admin account with such Email", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun activateUserAdmin(@RequestParam email: String): BaseResponse {
        log.info("activateUserAdmin(email = {})", email)
        userService.activateUserAdmin(email)
        return BaseResponse(ResponseCodes.OK)
    }

    @PreAuthorize("hasAuthority('BAN_USERS')")
    @PostMapping("/{id}/ban")
    @Operation(summary = "Ban user",
        description = "Ban user with a stated Id and reason. While doing so, there is a check if a current user has an authority to ban other members, user with this Id hadn't already been banned or current user is trying to ban itself")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User has been banned", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "401", description = "Not enough privileges to ban users", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "There is no user with such Id", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
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
    @Operation(summary = "Cancel ban status of the user",
        description = "Cancel ban status of the user with a stated Id and reason. While doing so, there is a check if a current user has an authority to unban other members, user with this Id hadn't already been unbanned or current user is trying to unban itself")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Ban status of the current user has been lifted", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "401", description = "Not enough privileges to unban users", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "There is no user with such Id", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun unbanUser(
            @PathVariable("id") id: Long
    ): BaseResponse {
        log.info("unbanUser(id = {})", id)
        userService.unbanUser(id)
        return BaseResponse(ResponseCodes.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}")
    @Operation(summary = "Update user",
        description = "Update information of the user with a stated Id. While doing so, there is a check if a current user has an authority to update info of other members and all input values are correct")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User info was updated", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "There is no user with such Id", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
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
    @Operation(summary = "Delete user",
        description = "Delete user with a stated Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User has been deleted", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "There is no user with such Id", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun deleteUser(@PathVariable("id") id: Long): BaseResponse {
        log.info("deleteUser(id = {})", id)
        userService.deleteUser(id)
        return BaseResponse(ResponseCodes.OK)
    }

    @PostMapping("/createPasswordRestoreToken")
    @Operation(summary = "Create password for current user",
        description = "Check if user account exists and generate new random password to send to stated email address")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Password was successfully generated ", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "There is no user with such Email", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun createPasswordRestoreToken(@RequestParam email: String): BaseResponse {
        log.info("createPasswordRestoreToken(email = {})", email)
        userService.createPasswordRestoreToken(email)
        return BaseResponse(ResponseCodes.OK)
    }

    @PostMapping("/restorePassword")
    @Operation(summary = "Restore password for current user",
        description = "Check if an old password exists and wasn't expired. If so replace an old password by a new one")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Password was successfully restored ", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "Your old password doesn't exist", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun restorePassword(@RequestParam token: String, @RequestParam newPassword: String): BaseResponse {
        log.info("restorePassword(token = {}, newPassword = ****)", token)
        userService.restorePassword(token, newPassword)
        return BaseResponse(ResponseCodes.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    @Operation(summary = "Get info about current user",
        description = "Get info about current user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Info about current user was successfully loaded", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun getCurrentUser(): ObjectResponse<UserDTO> {
        log.info("getCurrentUser()")
        val user = securityService.getCurrentUser() ?: return ObjectResponse(ResponseCodes.INVALID_CREDENTIALS)
        return ObjectResponse(UserDTO.fromUser(user))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get info about user with a stated Id",
        description = "Get info about user with a stated Id if it exists")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Info about user was successfully loaded", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "User doesn't exist", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun getUser(
       @PathVariable("id") id: Long
    ): ObjectResponse<UserDTO> {
        log.info("getUser(id = {})", id)
        val user = userService.getUser(id)
        return ObjectResponse(UserDTO.fromUser(user))
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/count")
    @Operation(summary = "Get info quantity of registered users",
        description = "Get info quantity of registered users")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Info about quantity of users was successfully loaded", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun countUsers(): NumberResponse {
        log.info("countUsers()")
        val count = userService.countUsers()
        return NumberResponse(count)
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/list")
    @Operation(summary = "Get list of registered users",
        description = "Get list of registered users")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Info about users was successfully loaded", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = UserDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun listUsers(
            @RequestParam(required = false, defaultValue = "0") page: Int,
            @RequestParam(required = false, defaultValue = "10") size: Int

    ): ObjectListResponse {
        log.info("listUsers(page = {}, size = {})", page, size)
        return ObjectListResponse(userService.getUsers(page, size).map { UserDTO.fromUser(it) })
    }
}