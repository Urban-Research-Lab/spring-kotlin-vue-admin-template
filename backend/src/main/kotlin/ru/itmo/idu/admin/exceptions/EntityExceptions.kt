package ru.itmo.idu.admin.exceptions

class EntityDoesNotExistException(message: String?) : Exception(message)

class EntityAlreadyExists(message: String?): Exception(message)