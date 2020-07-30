package ru.itmo.idu.admin.api_classes.dto

data class RoleDTO (
        val id: Long,
        val name: String,
        val permissions: List<String>
)