package ru.itmo.idu.admin.services

import com.nimbusds.oauth2.sdk.util.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.itmo.idu.admin.api_classes.LoginRequest
import ru.itmo.idu.admin.api_classes.UserRegistrationRequest
import ru.itmo.idu.admin.api_classes.UserUpdateRequest
import ru.itmo.idu.admin.exceptions.EntityAlreadyExists
import ru.itmo.idu.admin.exceptions.EntityDoesNotExistException
import ru.itmo.idu.admin.model.User
import ru.itmo.idu.admin.repositories.RoleRepository
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
        val roleRepository: RoleRepository,

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

    private fun addDefaultSuperAdmin() {
        var superAdmin = userRepository.findByEmail(defaultAdminLogin)
        if (superAdmin == null) {
            val role = roleRepository.findByName(defaultAdminRoleName)
                    ?: throw EntityDoesNotExistException("Did not find default admin role")
            log.info("Creating default admin user {}", defaultAdminLogin)
            superAdmin = User(
                    defaultAdminLogin,
                    passwordEncoder.encode(defaultAdminPass),
                    mutableListOf(role)
            )
            userRepository.save(superAdmin)
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
        val roles = userRegistrationRequest.roles.map { roleRepository.findById(it).orElseThrow{EntityDoesNotExistException("Role does not exist")} }

        var user = User(
                0,
                userRegistrationRequest.email,
                userRegistrationRequest.name,
                passwordEncoder.encode(userRegistrationRequest.password),
                roles.toMutableList(),
                System.currentTimeMillis()
        )
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
        currentUser = userRepository.save(currentUser)
        return currentUser
    }
}