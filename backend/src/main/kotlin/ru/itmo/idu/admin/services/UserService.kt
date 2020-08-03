package ru.itmo.idu.admin.services

import com.nimbusds.oauth2.sdk.util.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.idu.admin.api_classes.LoginRequest
import ru.itmo.idu.admin.api_classes.UserRegistrationRequest
import ru.itmo.idu.admin.api_classes.UserUpdateRequest
import ru.itmo.idu.admin.exceptions.EntityAlreadyExists
import ru.itmo.idu.admin.exceptions.EntityDoesNotExistException
import ru.itmo.idu.admin.model.User
import ru.itmo.idu.admin.repositories.UserRepository
import javax.annotation.PostConstruct

private val log = LoggerFactory.getLogger(UserService::class.java)

@Service
class UserService(
        @Autowired
        val userRepository: UserRepository,
        @Autowired
        val passwordEncoder: PasswordEncoder,
        @Autowired
        val roleService: RoleService,

        @Value("\${users.defaultUserRoleName}")
        val newUserRole: String,

        @Value("\${users.defaultAdminLogin}")
        val defaultAdminLogin: String,
        @Value("\${users.defaultAdminPass}")
        val defaultAdminPass: String,
        @Value("\${users.defaultAdminRoleName}")
        val defaultAdminRoleName: String
)
{

    @Transactional
    fun addDefaultSuperAdmin() {
        var superAdmin = userRepository.findByEmail(defaultAdminLogin)
        if (superAdmin == null) {
            val role = roleService.findByName(defaultAdminRoleName)
            log.info("Creating default admin user {}", defaultAdminLogin)
            superAdmin = User(
                    defaultAdminLogin,
                    passwordEncoder.encode(defaultAdminPass),
                    mutableListOf(role)
            )
            userRepository.save(superAdmin)
            role.users.add(superAdmin)
            roleService.save(role)
        }
    }

    @PostConstruct
    fun setUp() {
        addDefaultSuperAdmin()
    }

    fun registerUser(userRegistrationRequest: UserRegistrationRequest): User {
        log.info("Creating new user {} {}", userRegistrationRequest.email, userRegistrationRequest.name)
        val existingUser = userRepository.findByEmail(userRegistrationRequest.email)
        if (existingUser != null) {
            log.error("User with login {} already exists", userRegistrationRequest.email)
            throw EntityAlreadyExists("User already exists")
        }

        // todo: prevent from assigning some roles?
        val roles = userRegistrationRequest.roles.map { roleService.findById(it) }

        var user = User(
                0,
                userRegistrationRequest.email,
                userRegistrationRequest.name,
                passwordEncoder.encode(userRegistrationRequest.password),
                roles.toMutableList(),
                System.currentTimeMillis()
        )
        for (role in roles) {
            role.users.add(user)
        }
        roleService
        user = userRepository.save(user)
        log.info("User {} created", user.email)
        return user
    }

    /**
     * Checks given credentials, returns existing user or null if user not found or password incorrect
     */
    fun checkLogin(loginRequest: LoginRequest): User? {
        val user = userRepository.findByEmail(loginRequest.email) ?: return null
        return if (passwordEncoder.matches(loginRequest.password, user.password)) user else null
    }

    fun getUsers(page: Int, size: Int): List<User> {
        return userRepository.findAll(PageRequest.of(page, size)).toList()
    }

    fun getUser(id: Long): User {
        return userRepository.findById(id).orElseThrow{EntityDoesNotExistException("User $id not found")}
    }

    fun countUsers(): Long {
        return userRepository.count()
    }

    fun updateUser(id: Long, request: UserUpdateRequest): User {
        //todo check rights
        var currentUser = getUser(id)
        if (StringUtils.isNotBlank(request.newName)) {
            currentUser.displayName = request.newName;
        }
        if (StringUtils.isNotBlank(request.newPassword)) {
            currentUser.password = passwordEncoder.encode(request.newPassword)
        }
        if (request.newRoles != null) {
            // todo: prevent from assigning some roles?
            val roles = request.newRoles.map { roleService.findById(it) }.toMutableSet()
            val rolesToRemove = currentUser.roles.filter { roles.contains(it) }
            currentUser.roles.removeAll(rolesToRemove)
            rolesToRemove.forEach { it.users.remove(currentUser) }
            roleService.save(rolesToRemove)

            val newRoles = roles.filter { !currentUser.roles.contains(it) }
            currentUser.roles.addAll(newRoles)
            newRoles.forEach { it.users.add(currentUser) }
            roleService.save(newRoles)
        }
        currentUser = userRepository.save(currentUser)
        return currentUser
    }

    fun deleteUser(id: Long) {
        userRepository.delete(getUser(id))
        log.info("User {} deleted", id)
    }


}