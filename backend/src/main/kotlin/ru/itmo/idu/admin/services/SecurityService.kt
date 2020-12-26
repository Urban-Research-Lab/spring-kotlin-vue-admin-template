package ru.itmo.idu.admin.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.itmo.idu.admin.model.Permission
import ru.itmo.idu.admin.model.User
import ru.itmo.idu.admin.repositories.UserRepository


@Service
class SecurityService(
        @Autowired
        val userRepository: UserRepository
) {

    fun getCurrentUserLogin(): String? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        if (authentication.principal == "anonymousUser") return "anonymousUser"
        return (authentication.principal as org.springframework.security.core.userdetails.User).username
    }

    fun getCurrentUser(): User? {
        val login = getCurrentUserLogin() ?: return null
        return userRepository.findByEmail(login)
    }

    fun hasAuthority(user: User, permission: Permission): Boolean {
        return user.roles.any { it.permissions.contains(permission) }
    }
}