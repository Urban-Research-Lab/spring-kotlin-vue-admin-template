package ru.itmo.idu.admin.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.idu.admin.api_classes.dto.CreateRoleRequest
import ru.itmo.idu.admin.api_classes.dto.UpdateRoleRequest
import ru.itmo.idu.admin.exceptions.EntityDoesNotExistException
import ru.itmo.idu.admin.model.Permission
import ru.itmo.idu.admin.model.Role
import ru.itmo.idu.admin.repositories.RoleRepository
import ru.itmo.idu.admin.repositories.UserRepository
import javax.annotation.PostConstruct

private val log = LoggerFactory.getLogger(RoleService::class.java)

@Service
class RoleService (
    @Autowired
    val roleRepository: RoleRepository,
    @Value("\${users.defaultAdminRoleName}")
    val defaultAdminRoleName: String,
    @Value("\${users.defaultUserRoleName}")
    val defaultUserRoleName: String,

    @Autowired
    val userRepository: UserRepository
) {


    @PostConstruct
    fun initRoles() {
        var defaultAdminRole = roleRepository.findByName(defaultAdminRoleName)
        if (defaultAdminRole == null) {
            defaultAdminRole = Role(defaultAdminRoleName, ArrayList())
            log.info("Created default Role {}", defaultAdminRoleName)
        }
        defaultAdminRole.permissions.clear()
        defaultAdminRole.permissions.addAll(Permission.values())
        log.info("Default admin role has {} permissions", defaultAdminRole.permissions.size)

        var defaultUserRole = roleRepository.findByName(defaultUserRoleName)
        if (defaultUserRole == null) {
            defaultUserRole = Role(0, defaultUserRoleName, ArrayList(), ArrayList())
            roleRepository.save(defaultUserRole)
            log.info("Created user role $defaultUserRoleName")
        }

        roleRepository.save(defaultAdminRole)
    }

    fun createRole(request: CreateRoleRequest): Role {
        var role = Role(request.name, request.permissions.map { Permission.valueOf(it) }.toMutableList())
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

    @Transactional
    fun deleteRole(id: Long) {
        log.info("Deleting role {}", id)
        val role = findById(id)
        for (user in role.users) {
            user.roles.remove(role)
            userRepository.save(user)
        }
        roleRepository.delete(role)
        log.info("Role deleted")
    }

    fun updateRole(id: Long, request: UpdateRoleRequest): Role {
        var role = findById(id)
        role.permissions.clear()
        role.permissions.addAll( request.permissions.map { Permission.valueOf(it) })
        role = roleRepository.save(role)
        log.info("Role {} updated", id)
        return role
    }
}