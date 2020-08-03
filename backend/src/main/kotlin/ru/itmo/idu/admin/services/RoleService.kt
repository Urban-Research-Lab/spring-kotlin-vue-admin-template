package ru.itmo.idu.admin.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.itmo.idu.admin.api_classes.dto.CreateRoleRequest
import ru.itmo.idu.admin.exceptions.EntityDoesNotExistException
import ru.itmo.idu.admin.model.Permission
import ru.itmo.idu.admin.model.Role
import ru.itmo.idu.admin.repositories.RoleRepository
import javax.annotation.PostConstruct

private val log = LoggerFactory.getLogger(RoleService::class.java)

@Service
class RoleService (
    @Autowired
    val roleRepository: RoleRepository,
    @Value("\${users.defaultAdminRoleName}")
    val defaultAdminRoleName: String,
    @Value("\${users.defaultUserRoleName}")
    val defaultUserRoleName: String
) {


    @PostConstruct
    fun initRoles() {
        var defaultAdminRole = roleRepository.findByName(defaultAdminRoleName)
        if (defaultAdminRole == null) {
            defaultAdminRole = Role(0, defaultAdminRoleName, ArrayList())
            log.info("Created default Role {}", defaultAdminRoleName)
        }
        defaultAdminRole.permissions.clear()
        defaultAdminRole.permissions.addAll(Permission.values())
        log.info("Default admin role has {} permissions", defaultAdminRole.permissions.size)

        var defaultUserRole = roleRepository.findByName(defaultUserRoleName)
        if (defaultUserRole == null) {
            defaultUserRole = Role(0, defaultUserRoleName, ArrayList())
            roleRepository.save(defaultUserRole)
            log.info("Created user role ${defaultUserRoleName}")
        }

        roleRepository.save(defaultAdminRole)
    }

    fun createRole(request: CreateRoleRequest): Role {
        var role = Role(0, request.name, request.permissions.map { Permission.valueOf(it) }.toMutableList())
        role = roleRepository.save(role)
        log.info("Role {} created", role.name)
        return role
    }

    fun listRoles(): MutableList<Role> {
        return roleRepository.findAll()
    }

    fun findByName(name: String): Role {
        return roleRepository.findByName(name)
                ?: throw EntityDoesNotExistException("Did not find role $name")
    }

    fun findById(id: Long): Role {
        return roleRepository.findById(id).orElseThrow { EntityDoesNotExistException("Did not find role $id") }
    }
}