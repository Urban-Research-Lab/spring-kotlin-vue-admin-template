package ru.itmo.idu.admin.api_classes

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

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
)