package ru.itmo.idu.admin.controllers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.idu.admin.api_classes.ObjectListResponse
import ru.itmo.idu.admin.api_classes.ObjectResponse
import ru.itmo.idu.admin.api_classes.dto.CreateRoleRequest
import ru.itmo.idu.admin.api_classes.dto.RoleDTO
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
    fun getRole(@PathVariable("id") id: Long): ObjectResponse {
        TODO()
    }

    @PostMapping("/{id}")
    fun updateRole(
            @PathVariable("id") id: Long,
            @RequestBody updated: RoleDTO

    ): ObjectResponse {
        TODO()
    }

    @GetMapping("/list")
    fun listRoles(): ObjectListResponse {
        return ObjectListResponse(
                roleService.listRoles().map { RoleDTO.fromRole(it) }
        )
    }

    @GetMapping("/permissions")
    fun listPermissions(): ObjectListResponse {
        return ObjectListResponse(Permission.values().map { it.name })
    }

    @PostMapping("/create")
    fun createRole(@RequestBody request: CreateRoleRequest): ObjectResponse {
        log.info("createRole(request = {})", request)
        val role = roleService.createRole(request)
        return ObjectResponse(RoleDTO.fromRole(role))
    }

    fun createRole() {
        TODO()
    }

}