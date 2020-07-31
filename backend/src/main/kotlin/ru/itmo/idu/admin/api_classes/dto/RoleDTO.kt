package ru.itmo.idu.admin.api_classes.dto

import ru.itmo.idu.admin.model.Role

data class RoleDTO (
        val id: Long,
        val name: String,
        val permissions: List<String>
) {
    companion object {
        fun fromRole(role: Role): RoleDTO {
            return RoleDTO(role.id, role.name, role.permissions.map { it.name })
        }
    }
}