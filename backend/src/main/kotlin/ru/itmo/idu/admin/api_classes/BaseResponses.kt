package ru.itmo.idu.admin.api_classes

enum class ResponseCodes(val code: Int, val message: String) {
    OK(0, "Completed Successfully"),
    INTERNAL_ERROR(1, "Internal Error"),

    INSUFFICIENT_PRIVILEGES(101, "User does not have access to this operation"),
    INVALID_CREDENTIALS(102, "User login, password or oauth token is incorrect"),
    ACCOUNT_BANNED(103, "This user account is banned"),
    INVALID_INPUT_OUTPUT(104, "Failed to create or download a file"),
    ENTITY_ALREADY_EXISTS(105, "Object already exists"),
    ENTITY_DOESNT_EXIST(106, "Object does not exist"),
    ILLEGAL_ENTITY_STATE(107, "Object in this state can not be moderated"),
    ILLEGAL_ARGUMENT(108, "Object can not be operated (invalid format or invalid arguments or file already exists)"),
    INSUFFICIENT_PRIVILEGES_OR_NO_USER(109, "User is not logged in or user can not do this operation"),
    TOKEN_EXPIRED(111, "Token expired")
}

open class BaseResponse(val code: Int, val message: String) {

    constructor(rc: ResponseCodes) : this(rc.code, rc.message)

    constructor(rc: ResponseCodes, customMessage: String): this(rc.code, customMessage)

    fun isOk(): Boolean = this.code == ResponseCodes.OK.code
}

class IdResponse(val id: Long, code: Int, message: String)
    : BaseResponse(code, message) {

    constructor(rc: ResponseCodes) : this(0, rc.code, rc.message)

    constructor(id: Long, rc: ResponseCodes) : this(id, rc.code, rc.message)

    constructor(id: Long): this(id, ResponseCodes.OK)
}

class NumberResponse(val number: Long, code: Int, message: String)
    : BaseResponse(code, message) {

    constructor(rc: ResponseCodes) : this(0, rc.code, rc.message)

    constructor(number: Long, rc: ResponseCodes) : this(number, rc.code, rc.message)

    constructor(number: Long): this(number, ResponseCodes.OK)
}

class StringResponse(val result: String?, code: Int, message: String)
    : BaseResponse(code, message) {

    constructor(rc: ResponseCodes) : this(null, rc.code, rc.message)

    constructor(result: String): this(result, ResponseCodes.OK.code, ResponseCodes.OK.message)
}

class ObjectListResponse(val objects: List<Any>?, code: Int, message: String)
    : BaseResponse(code, message) {

    constructor(rc: ResponseCodes) : this(null, rc.code, rc.message)

    constructor(objects: List<Any>): this(objects, ResponseCodes.OK.code, ResponseCodes.OK.message)
}

class ObjectResponse<E>(val data: E?, code: Int, message: String)
    : BaseResponse(code, message) {

    constructor(rc: ResponseCodes) : this(null, rc.code, rc.message)

    constructor(data: E?): this(data, ResponseCodes.OK.code, ResponseCodes.OK.message)
}
