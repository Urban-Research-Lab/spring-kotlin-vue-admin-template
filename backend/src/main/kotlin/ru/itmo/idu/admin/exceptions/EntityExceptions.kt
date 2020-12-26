package ru.itmo.idu.admin.exceptions

class EntityDoesNotExistException(message: String?) : Exception(message)

class EntityAlreadyExists(message: String?): Exception(message)

class EntityAccessDeniedException(message: String?): Exception(message)

class IllegalEntityStateException(message: String?): Exception(message)

class InsufficientPrivilegesException(message: String?): Exception(message)

class PasswordRestoreTokenExpiredException(message: String?): Exception(message)