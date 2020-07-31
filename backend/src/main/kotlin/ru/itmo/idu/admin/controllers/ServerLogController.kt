package ru.itmo.idu.admin.controllers

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.idu.admin.api_classes.ObjectListResponse
import ru.itmo.idu.admin.services.LogService

@RestController
@PreAuthorize("hasAuthority('SERVER_ADMIN')")
@RequestMapping("/api/v1/logs")
class ServerLogController(
        val logService: LogService
) {

    @GetMapping(path = ["", "/"])
    fun getLogs(): ObjectListResponse {
        return ObjectListResponse(logService.getLogEntries())
    }

}