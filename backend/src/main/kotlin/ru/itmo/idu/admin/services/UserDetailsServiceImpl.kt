package ru.itmo.idu.admin.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.idu.admin.model.UserStatus
import ru.itmo.idu.admin.repositories.UserRepository

@Service
class UserDetailsServiceImpl: UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username) ?: throw UsernameNotFoundException("User '$username' not found")

        val permissions = user.roles.flatMap { it.permissions }
        val authorities: List<GrantedAuthority> = permissions.distinct().map { SimpleGrantedAuthority(it.name) }

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.password)
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(user.status == UserStatus.BANNED) //todo: handle LockedException
                .credentialsExpired(false)
                .disabled(false)
                .build()
    }
}