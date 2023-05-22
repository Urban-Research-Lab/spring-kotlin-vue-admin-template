package ru.itmo.idu.admin.repositories

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.itmo.idu.admin.model.User

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmail(@Param("email") email: String): Boolean

    fun findByEmail(@Param("email") email: String): User?

    @Transactional
    fun deleteByEmail(@Param("email") email: String)

}