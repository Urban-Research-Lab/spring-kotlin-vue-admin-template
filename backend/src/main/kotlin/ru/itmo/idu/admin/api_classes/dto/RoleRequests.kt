package ru.itmo.idu.admin.api_classes.dto

data class CreateRoleRequest(
        val name: String,
        val permissions: List<String>
)

data class UpdateRoleRequest(
        val permissions: List<String>
)