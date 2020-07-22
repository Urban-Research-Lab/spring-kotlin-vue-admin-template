package ru.itmo.idu.admin.api_classes

enum class ResponseCodes(val code: Int, val message: String) {
    OK(0, "Completed Successfully"),
    INTERNAL_ERROR(1, "Internal Error"),

    INSUFFICIENT_PRIVILEGES(101, "User does not have access to this operation"),
    INVALID_CREDENTIALS(102, "User not found or password is incorrect")
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

class ObjectResponse(val data: Any?, code: Int, message: String)
    : BaseResponse(code, message) {

    constructor(rc: ResponseCodes) : this(null, rc.code, rc.message)

    constructor(data: Any?): this(data, ResponseCodes.OK.code, ResponseCodes.OK.message)
}