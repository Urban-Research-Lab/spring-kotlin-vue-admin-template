package ru.itmo.idu.admin.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import ru.itmo.idu.admin.api_classes.BaseResponse
import ru.itmo.idu.admin.api_classes.ResponseCodes
import ru.itmo.idu.admin.exceptions.*
import java.io.IOException
import java.lang.reflect.UndeclaredThrowableException

private val log = LoggerFactory.getLogger(RestErrorHandler::class.java)

@ControllerAdvice
class RestErrorHandler {

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun processAccessDeniedError(ex: Throwable): BaseResponse {
        // we need to return 401 as it is expected behavior for frontend when token is invalid or requires renewal
        return getResponse(ex)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun processValidationError12(ex: Throwable): BaseResponse {
        return getResponse(ex)
    }

    fun getResponse(e: Throwable): BaseResponse {
        if (e is UndeclaredThrowableException) {
            return getResponse(e.cause!!)
        }
        when (e){
            is EntityDoesNotExistException -> return BaseResponse(ResponseCodes.ENTITY_DOESNT_EXIST, e.message.orEmpty())
            is EntityAccessDeniedException -> return BaseResponse(ResponseCodes.INSUFFICIENT_PRIVILEGES)
            is EntityAlreadyExists -> return BaseResponse(ResponseCodes.ENTITY_ALREADY_EXISTS)
            is IllegalEntityStateException -> return BaseResponse(ResponseCodes.ILLEGAL_ENTITY_STATE)
            is AccessDeniedException -> return BaseResponse(ResponseCodes.INSUFFICIENT_PRIVILEGES)
            is IllegalArgumentException -> return BaseResponse(ResponseCodes.ILLEGAL_ARGUMENT)
            is UsernameNotFoundException -> return BaseResponse(ResponseCodes.INVALID_CREDENTIALS)
            is IllegalStateException -> return BaseResponse(ResponseCodes.INSUFFICIENT_PRIVILEGES_OR_NO_USER)
            is IOException -> return BaseResponse(ResponseCodes.INVALID_INPUT_OUTPUT)
            is PasswordRestoreTokenExpiredException ->  return BaseResponse(ResponseCodes.TOKEN_EXPIRED, e.message.orEmpty())
            else -> {
                log.error("Unexpected error type, returning INTERNAL_ERROR", e)
                return BaseResponse(ResponseCodes.INTERNAL_ERROR)
            }
        }
    }
}