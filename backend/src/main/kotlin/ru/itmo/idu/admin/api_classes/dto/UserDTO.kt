package ru.itmo.idu.admin.api_classes.dto

import ru.itmo.idu.admin.model.User
import ru.itmo.idu.admin.model.UserStatus

data class UserDTO(
        val id: Long,
        val email: String,
        val name: String?,
        val authorities: List<String>,
        val roles: List<RoleDTO>,
        val registrationTimestamp: Long,
        val status: String
) {
    companion object {
        fun fromUser(user: User): UserDTO {
            return UserDTO(
                    user.id,
                    user.email,
                    user.displayName,
                    user.roles.flatMap { it.permissions }.map { it.name },
                    user.roles.map { RoleDTO.fromRoleSimplified(it) },
                    user.registrationTimestamp,
                    user.status.name
            )
        }

        fun fromUserSimple(user: User): UserDTO {
            return UserDTO(user.id, user.email, user.displayName, listOf(), listOf(), user.registrationTimestamp, user.status.name)
        }
    }
}