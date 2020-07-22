package ru.itmo.idu.admin.api_classes

import ru.itmo.idu.admin.api_classes.dto.UserDTO

class LoginResponse(
        responseCode: ResponseCodes,
        val user: UserDTO,
        val token: String
): BaseResponse(responseCode)