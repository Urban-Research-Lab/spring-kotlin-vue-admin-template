package ru.itmo.idu.admin.api_classes.dto

data class LogEntryDTO (
        val level: String,
        val timestamp: Long,
        val message: String
)