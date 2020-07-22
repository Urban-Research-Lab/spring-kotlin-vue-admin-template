package ru.itmo.idu.admin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import ru.itmo.idu.admin.model.Role

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(@Param("name") name: String): Role?
}