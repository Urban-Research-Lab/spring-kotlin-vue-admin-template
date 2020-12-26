package ru.itmo.idu.admin.services

import com.nimbusds.oauth2.sdk.util.StringUtils
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.idu.admin.api_classes.LoginRequest
import ru.itmo.idu.admin.api_classes.UserRegistrationRequest
import ru.itmo.idu.admin.api_classes.UserUpdateRequest
import ru.itmo.idu.admin.exceptions.EntityAlreadyExists
import ru.itmo.idu.admin.exceptions.EntityDoesNotExistException
import ru.itmo.idu.admin.exceptions.InsufficientPrivilegesException
import ru.itmo.idu.admin.exceptions.PasswordRestoreTokenExpiredException
import ru.itmo.idu.admin.model.*
import ru.itmo.idu.admin.repositories.PasswordRestoreTokenRepository
import ru.itmo.idu.admin.repositories.UserActivationTokenRepository
import ru.itmo.idu.admin.repositories.UserRepository
import java.util.*


private val log = LoggerFactory.getLogger(UserService::class.java)

@Service
class UserService(
        @Autowired
        val userRepository: UserRepository,
        @Autowired
        val passwordEncoder: PasswordEncoder,
        @Autowired
        val roleService: RoleService,
        @Autowired
        val userActivationTokenRepository: UserActivationTokenRepository,

        @Autowired
        val emailService: EmailService,
        @Autowired
        val messageSource: MessageSource,
        @Autowired
        val passwordRestoreTokenRepository: PasswordRestoreTokenRepository,
        @Autowired
        val securityService: SecurityService,

        @Value("\${users.defaultAdminLogin}")
        val defaultAdminLogin: String,
        @Value("\${users.defaultAdminPass}")
        val defaultAdminPass: String,
        @Value("\${users.defaultAdminRoleName}")
        val defaultAdminRoleName: String,
        @Value("\${frontend.location}")
        val frontendLocation: String,
        @Value("\${mail.locale}")
        val mailLocale: String,
        @Value("\${passwordRestoreToken.expireMillis}")
        val passwordRestoreTokenExpireMillis: Long,
        @Value("\${users.defaultUserRoleName}")
        val defaultUserRoleName: String,

        @Value("\${features.oauthEnabled}") val oauthEnabled: Boolean,
        @Value("\${features.registrationEnabled}") val registrationEnabled: Boolean

)
{
    // we can not use @PostConstruct together with @Transactional, so we have to make initialization this way
    @EventListener(ContextRefreshedEvent::class)
    fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBean(UserService::class.java).setUp()
    }

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
            role.users.add(superAdmin)
            userRepository.save(superAdmin)
            roleService.save(role)
        }
    }

    @Transactional
    fun setUp() {
        addDefaultSuperAdmin()
    }

    fun getDefaultUserRole(): Role {
        return roleService.findByName(defaultUserRoleName)
    }

    fun registerUser(userRegistrationRequest: UserRegistrationRequest): User {
        val currentUser = securityService.getCurrentUser()
        if (!registrationEnabled && (currentUser == null || !hasAuthority(currentUser, Permission.MANAGE_USERS))) {
            log.error("Registration is disabled, only admin user can create new users")
            throw InsufficientPrivilegesException("Only admin users can create new accounts")
        }
        log.info("Creating new user {} {}", userRegistrationRequest.email, userRegistrationRequest.name)
        val existingUser = userRepository.findByEmail(userRegistrationRequest.email)
        if (existingUser != null) {
            log.error("User with login {} already exists", userRegistrationRequest.email)
            throw EntityAlreadyExists("User already exists")
        }

        // todo: prevent from assigning some roles?
        val roles = if (userRegistrationRequest.roles != null && userRegistrationRequest.roles.isNotEmpty()) {
            // only user with MANAGE_USERS permission can assign roles
            val currentUser = securityService.getCurrentUser()
            if (currentUser == null || !securityService.hasAuthority(currentUser, Permission.MANAGE_USERS)) {
                log.error("MANAGE_USERS permission required to create user with non-default role list")
                throw InsufficientPrivilegesException("You need MANAGE_USERS permission to create users with non-default list of roles")
            }
            userRegistrationRequest.roles.map { roleService.findById(it) }
        } else {
            listOf(getDefaultUserRole())
        }

        var user = User(
                0,
                userRegistrationRequest.email,
                userRegistrationRequest.name,
                passwordEncoder.encode(userRegistrationRequest.password),
                roles.toMutableList(),
                System.currentTimeMillis(),
                null,
                null,
                UserStatus.REGISTERED
        )
        for (role in roles) {
            role.users.add(user)
        }
        roleService
        user = userRepository.save(user)
        val email = user.email
        log.info("User {} created. Sending activation email...", email)
        val token = RandomStringUtils.randomAlphanumeric(20).toLowerCase()
        val t = UserActivationToken(token, email)
        userActivationTokenRepository.save(t)
        val link = "$frontendLocation/activateUser?token=$token"
        emailService.sendSimpleMessage(email, messageSource.getMessage("mail.activation.subject", arrayOf(link), Locale.forLanguageTag(mailLocale)),
                messageSource.getMessage("mail.activation.text", arrayOf(link), Locale.forLanguageTag(mailLocale)))
        return user
    }

    fun activateUser(token: String): String {
        val t = userActivationTokenRepository.findByIdOrNull(token) ?: throw EntityDoesNotExistException("Token not found")
        val user = userRepository.findByEmail(t.email) ?: throw EntityDoesNotExistException("User ${t.email} not found")
        val email = t.email
        if (user.status != UserStatus.REGISTERED) {
            log.info("User already activated")
            return email
        }
        user.status = UserStatus.ACTIVE
        userRepository.save(user)
        log.info("User ${user.email} activated")
        userActivationTokenRepository.delete(t)
        return email
    }

    fun activateUserAdmin(email: String) {
        val user = userRepository.findByEmail(email) ?: throw EntityDoesNotExistException("User $email not found")
        if (user.status != UserStatus.REGISTERED) {
            log.info("User already activated")
            return
        }
        user.status = UserStatus.ACTIVE
        userRepository.save(user)
        log.info("User activated")
    }

    fun createPasswordRestoreToken(email: String): String {
        getUser(email) // ensure user exists
        val token = RandomStringUtils.randomAlphanumeric(20).toLowerCase()
        val t = PasswordRestoreToken(email, token)
        passwordRestoreTokenRepository.save(t)
        val link = "$frontendLocation/resetPassword?token=$token"
        emailService.sendSimpleMessage(email, messageSource.getMessage("mail.passwordRestore.subject", arrayOf(link), Locale.forLanguageTag(mailLocale)),
                messageSource.getMessage("mail.passwordRestore.text", arrayOf(link), Locale.forLanguageTag(mailLocale)))
        return token
    }

    fun restorePassword(token: String, newPassword: String) {
        val pt = passwordRestoreTokenRepository.findByIdOrNull(token) ?: throw EntityDoesNotExistException("Token not found")
        if (pt.createdAt.time + passwordRestoreTokenExpireMillis < System.currentTimeMillis()) {
            val createdAt = pt.createdAt
            passwordRestoreTokenRepository.delete(pt)
            throw PasswordRestoreTokenExpiredException("Token was created at $createdAt and already expired")
        }
        changePassword(pt.email, newPassword)
        passwordRestoreTokenRepository.delete(pt)
    }

    fun changePassword(email: String, newPassword: String) {
        val user = userRepository.findByEmail(email) ?: throw EntityDoesNotExistException("User $email not found")
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
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

    fun getUser(email: String): User {
        return userRepository.findByEmail(email) ?: throw EntityDoesNotExistException("User $email not found")
    }

    fun getUser(id: Long): User {
        return userRepository.findById(id).orElseThrow{EntityDoesNotExistException("User $id not found")}
    }

    fun countUsers(): Long {
        return userRepository.count()
    }

    fun updateUser(id: Long, request: UserUpdateRequest): User {

        var userToChange = getUser(id)
        val currentUser = securityService.getCurrentUser()
        if (userToChange.id != userToChange.id && !securityService.hasAuthority(userToChange, Permission.MANAGE_USERS)) {
            log.error("You need MANAGE_USERS permission to change users other than yourself")
            throw InsufficientPrivilegesException("You can not edit this user")
        }
        if ((request.newRoles != null && request.newRoles.isNotEmpty() && request.newRoles != userToChange.roles) && !securityService.hasAuthority(userToChange, Permission.MANAGE_USERS)) {
            log.error("You need MANAGE_USERS permission to change list of roles for a user")
            throw InsufficientPrivilegesException("You are not allowed to change list of roles")
        }

        if (StringUtils.isNotBlank(request.newName)) {
            userToChange.displayName = request.newName;
        }
        if (StringUtils.isNotBlank(request.newPassword)) {
            userToChange.password = passwordEncoder.encode(request.newPassword)
        }
        if (request.newRoles != null) {

            val roles = request.newRoles.map { roleService.findById(it) }.toMutableSet()
            val rolesToRemove = userToChange.roles.filter { !roles.contains(it) }
            userToChange.roles.removeAll(rolesToRemove)
            rolesToRemove.forEach { it.users.remove(userToChange) }
            roleService.save(rolesToRemove)

            val newRoles = roles.filter { !userToChange.roles.contains(it) }
            userToChange.roles.addAll(newRoles)
            newRoles.forEach { it.users.add(userToChange) }
            roleService.save(newRoles)
        }
        userToChange = userRepository.save(userToChange)
        return userToChange
    }

    fun deleteUser(id: Long) {
        userRepository.delete(getUser(id))
        log.info("User {} deleted", id)
    }

    fun saveUser(user: User): User = userRepository.save(user)

    fun getAuthorities(user: User): List<GrantedAuthority> {
        val permissions = user.roles.flatMap { it.permissions }
        return permissions.distinct().map { SimpleGrantedAuthority(it.name) }
    }

    @Transactional
    fun createUserForOAuthLogin(provider: String, userProperties: Map<String, Any>): UserDetails {
        if (!oauthEnabled) {
            log.error("OAuth logins disabled in config, can not create new user from OAuth provider")
            throw IllegalStateException("OAuth logins are disabled in config")
        }
        val email = userProperties["email"].toString()
        val name = userProperties["name"].toString()
        val existingUser = userRepository.findByEmail(email)
        val user = if (existingUser == null) {
            val defaultRole = roleService.findByName(defaultUserRoleName)
            log.info("Creating new user $email from OAuth login")
            val user = User(
                    0L,
                    email,
                    name,
                    "",
                    mutableListOf(defaultRole),
                    System.currentTimeMillis(),
                    provider,
                    null,
                    UserStatus.ACTIVE

            )
            user.status = UserStatus.ACTIVE
            userRepository.save(user)
        } else {
            existingUser
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.email)
                .password("")
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(user.status == UserStatus.BANNED)
                .credentialsExpired(false)
                .disabled(false)
                .build()
    }

    fun hasAuthority(user: User?, permission: Permission): Boolean {
        if (user == null) {
            return false
        }
        return user.roles.any { it.permissions.contains(permission) }
    }

    @Transactional
    fun banUser(userId: Long, reason: String) {
        log.info("Banning user {} with reason {}", userId, reason)
        val currentUser = securityService.getCurrentUser() ?: throw throw InsufficientPrivilegesException("This action requires a login")
        if (!hasAuthority(currentUser, Permission.BAN_USERS)) {
            log.error("Current user does not have permission to ban other users")
            throw InsufficientPrivilegesException("You are not permitted to ban users")
        }
        val userToBan = getUser(userId)
        if (userToBan.status == UserStatus.BANNED) {
            log.info("User is already banned")
            return
        }
        if (userToBan == currentUser) {
            log.error("Can not ban self")
            throw IllegalArgumentException("Can not ban self")
        }
        userToBan.status = UserStatus.BANNED
        userToBan.banInfo = BanInfo(0L, currentUser, userToBan, System.currentTimeMillis(), reason)
        userRepository.save(userToBan)
    }

    @Transactional
    fun unbanUser(userId: Long) {
        log.info("Unbanning user {} ", userId)
        val currentUser = securityService.getCurrentUser() ?: throw throw InsufficientPrivilegesException("This action requires a login")
        if (!hasAuthority(currentUser, Permission.BAN_USERS)) {
            log.error("Current user does not have permission to unban")
            throw InsufficientPrivilegesException("You are not permitted to unban users")
        }
        val userToUnban = getUser(userId)
        if (userToUnban.status != UserStatus.BANNED) {
            log.info("User already unbanned")
            return
        }
        userToUnban.status = UserStatus.ACTIVE
        userToUnban.banInfo = null
        userRepository.save(userToUnban)
    }
}