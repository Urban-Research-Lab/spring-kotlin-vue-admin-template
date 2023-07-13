package ru.itmo.idu.admin.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.idu.admin.api_classes.BaseResponse
import ru.itmo.idu.admin.api_classes.ObjectListResponse
import ru.itmo.idu.admin.api_classes.ObjectResponse
import ru.itmo.idu.admin.api_classes.ResponseCodes
import ru.itmo.idu.admin.api_classes.dto.CreateRoleRequest
import ru.itmo.idu.admin.api_classes.dto.RoleDTO
import ru.itmo.idu.admin.api_classes.dto.UpdateRoleRequest
import ru.itmo.idu.admin.model.Permission
import ru.itmo.idu.admin.services.RoleService

private val log = LoggerFactory.getLogger(RolesController::class.java)

@RestController
@RequestMapping("/api/v1/role")
@PreAuthorize("hasAuthority('MANAGE_ROLES')")
class RolesController(
        @Autowired
        val roleService: RoleService
) {

    @GetMapping("/{id}")
    @Operation(summary = "Get the role of a user",
        description = "Get the role of a user with a stated Id",
        security = [SecurityRequirement(name = "AuthToken")])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Info about role was successfully loaded", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = RoleDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "User doesn't exist", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun getRole(@PathVariable("id") id: Long): ObjectResponse<RoleDTO> {
        return ObjectResponse(RoleDTO.fromRole(roleService.findById(id)))
    }

    @PostMapping("/{id}")
    @Operation(summary = "Update the role of a user",
        description = "Update the role of a user with a stated Id",
        security = [SecurityRequirement(name = "AuthToken")])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Info about role was successfully loaded", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = RoleDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "User doesn't exist", content = [
            (Content(schema = Schema(hidden = true)))])])
    fun updateRole(
            @PathVariable("id") id: Long,
            @RequestBody request: UpdateRoleRequest
    ): ObjectResponse<RoleDTO> {
        log.info("updateRole(id = {}, request = {})")
        val result = roleService.updateRole(id, request)
        return ObjectResponse(RoleDTO.fromRole(result))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete the role of a user",
        description = "Delete the role of a user with a stated Id",
        security = [SecurityRequirement(name = "AuthToken")])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Info about role was successfully removed", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "404", description = "User doesn't exist", content = [
            (Content(schema = Schema(hidden = true)))])])
    fun deleteRole(@PathVariable("id") id: Long): BaseResponse {
        log.info("deleteRole(id = {})")
        roleService.deleteRole(id)
        return BaseResponse(ResponseCodes.OK)
    }

    @GetMapping("/list")
    @Operation(summary = "Get list of roles",
        description = "Get list of all roles",
        security = [SecurityRequirement(name = "AuthToken")])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lists were successfully loaded", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = RoleDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun listRoles(): ObjectListResponse {
        return ObjectListResponse(
                roleService.listRoles().map { RoleDTO.fromRole(it) }
        )
    }

    @GetMapping("/permissions")
    @Operation(summary = "Get list of permissions",
        description = "Get list of all permissions",
        security = [SecurityRequirement(name = "AuthToken")])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lists were successfully loaded", content = [
            (Content(mediaType = "application/json", array = (
                    ArraySchema(schema = Schema(implementation = RoleDTO::class)))))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun listPermissions(): ObjectListResponse {
        return ObjectListResponse(Permission.values().map { it.name })
    }

    @PostMapping("/create")
    @Operation(summary = "Create a role",
        description = "Create a role with specific name and permissions",
        security = [SecurityRequirement(name = "AuthToken")])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Role was created", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = RoleDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])])
    fun createRole(@RequestBody request: CreateRoleRequest): ObjectResponse<RoleDTO> {
        log.info("createRole(request = {})", request)
        val role = roleService.createRole(request)
        return ObjectResponse(RoleDTO.fromRole(role))
    }

}