package ru.itmo.idu.admin.api_classes

import ru.itmo.idu.admin.api_classes.dto.UserDTO

class BanInfoDTO(
        val banId: Long,
        val reason: String
)

class LoginResponse(
        code: Int,
        message: String,
        val user: UserDTO?,
        val token: String?,
        val banInfo: BanInfoDTO?
): BaseResponse(code, message) {
    constructor(): this(ResponseCodes.OK, null, null)

    constructor(responseCodes: ResponseCodes): this(responseCodes, null, null)

    constructor(responseCodes: ResponseCodes, user: UserDTO?, token: String?): this(responseCodes.code, responseCodes.message, user, token, null)
}