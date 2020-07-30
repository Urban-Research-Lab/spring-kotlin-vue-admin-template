package ru.itmo.idu.admin.api_classes.dto

import ru.itmo.idu.admin.model.User

data class UserDTO(
        val id: Long,
        val email: String,
        val name: String?,
        val authorities: List<String>
) {
    companion object {
        fun fromUser(user: User): UserDTO {
            return UserDTO(
                    user.id,
                    user.email,
                    user.displayName,
                    user.roles.flatMap { it.permissions }.map { it.name }
            )
        }
    }
}