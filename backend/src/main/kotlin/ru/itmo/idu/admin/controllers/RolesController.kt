package ru.itmo.idu.admin.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.idu.admin.api_classes.ObjectResponse
import ru.itmo.idu.admin.api_classes.dto.RoleDTO
import ru.itmo.idu.admin.services.RoleService

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

    fun listRoles() {
        TODO()
    }

    fun createRole() {
        TODO()
    }

}