package ru.itmo.idu.admin.api_classes

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class LoginRequest (
        val email: String,
        val password: String
)

data class UserRegistrationRequest(
        @NotEmpty
        var name: String,
        @Email
        @NotEmpty
        var email: String,
        @NotEmpty
        var password: String,
        val roles: List<Long>
)

data class UserUpdateRequest(
        val newName: String?,
        val newPassword: String?,
        val newRoles: List<Long>?
) {
        override fun toString(): String {
                val passwordString = if (newPassword == null) "null" else "*****"
                return "UserUpdateRequest(newName=$newName, newPassword=${passwordString}, newRoles=$newRoles)"
        }
}