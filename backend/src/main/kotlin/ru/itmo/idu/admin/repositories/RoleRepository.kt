package ru.itmo.idu.admin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.itmo.idu.admin.model.Role

@Repository
interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(@Param("name") name: String): Role?
}