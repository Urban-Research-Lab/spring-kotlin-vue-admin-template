package ru.itmo.idu.admin.api_classes.dto

data class CreateRoleRequest(
        val name: String,
        val permissions: List<String>
)