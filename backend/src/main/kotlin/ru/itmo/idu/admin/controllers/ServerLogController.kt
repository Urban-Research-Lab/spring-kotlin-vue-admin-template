package ru.itmo.idu.admin.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.idu.admin.api_classes.ObjectListResponse
import ru.itmo.idu.admin.api_classes.dto.LogEntryDTO
import ru.itmo.idu.admin.services.LogService

@RestController
@PreAuthorize("hasAuthority('SERVER_ADMIN')")
@RequestMapping("/api/v1/logs")
class ServerLogController(
        val logService: LogService
) {

    @GetMapping(path = ["", "/"])
    @Operation(summary = "Get logs about events",
        description = "Get logs about events",
        security = [SecurityRequirement(name = "AuthToken")])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Logs were successfully loaded", content = [
            (Content(mediaType = "application/json", schema = Schema(implementation = LogEntryDTO::class)))]),
        ApiResponse(responseCode = "400", description = "Bad request", content = [
            (Content(schema = Schema(hidden = true)))]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [
            (Content(schema = Schema(hidden = true)))])]
    )
    fun getLogs(): ObjectListResponse {
        return ObjectListResponse(logService.getLogEntries())
    }

}