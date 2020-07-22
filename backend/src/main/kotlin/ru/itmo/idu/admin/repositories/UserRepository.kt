package ru.itmo.idu.admin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import ru.itmo.idu.admin.model.User
import javax.transaction.Transactional

interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmail(@Param("email") email: String): Boolean

    fun findByEmail(@Param("email") email: String): User?

    @Transactional
    fun deleteByEmail(@Param("email") email: String)

}