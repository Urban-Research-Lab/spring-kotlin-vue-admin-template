package ru.itmo.idu.admin.integration

import jakarta.transaction.Transactional
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import ru.itmo.idu.admin.api_classes.LoginRequest
import ru.itmo.idu.admin.api_classes.UserRegistrationRequest
import ru.itmo.idu.admin.api_classes.UserUpdateRequest
import ru.itmo.idu.admin.api_classes.dto.CreateRoleRequest
import ru.itmo.idu.admin.exceptions.EntityDoesNotExistException
import ru.itmo.idu.admin.exceptions.InsufficientPrivilegesException
import ru.itmo.idu.admin.exceptions.PasswordRestoreTokenExpiredException
import ru.itmo.idu.admin.model.PasswordRestoreToken
import ru.itmo.idu.admin.model.Role
import ru.itmo.idu.admin.model.UserStatus
import ru.itmo.idu.admin.repositories.PasswordRestoreTokenRepository
import ru.itmo.idu.admin.repositories.UserActivationTokenRepository
import ru.itmo.idu.admin.services.RoleService
import java.util.*

@Transactional
class UserServiceIT : BaseIntegrationTest() {

    @Autowired
    lateinit var roleService: RoleService

    @Autowired
    lateinit var userActivationTokenRepository: UserActivationTokenRepository

    @Autowired
    lateinit var passwordRestoreTokenRepository: PasswordRestoreTokenRepository

    @Test
    @WithMockUser("1@example.com")
    fun testUpdateUserRoles() {
        var user = userService.registerUser(UserRegistrationRequest(
                "userForRoleUpdate",
                "user@example.com",
                "12345",
                emptyList()
        ))

        Assert.assertEquals(user.roles.size, 1)

        val role1 = roleService.createRole(CreateRoleRequest("role1", emptyList()))
        val role2 = roleService.createRole(CreateRoleRequest("role2", emptyList()))
        val role3 = roleService.createRole(CreateRoleRequest("role3", emptyList()))


        userService.updateUser(user.id, UserUpdateRequest(
                null,
                null,
                listOf(role1.id, role2.id)
        ))

        user = userService.getUser(user.id)

        Assert.assertEquals(2, user.roles.size)

        user = userService.updateUser(user.id, UserUpdateRequest(
                null,
                null,
                listOf(role3.id, role2.id)
        ))

        Assert.assertEquals(2, user.roles.size)

        Assert.assertTrue(user.roles.contains(role2))
        Assert.assertTrue(user.roles.contains(role3))
        Assert.assertFalse(user.roles.contains(role1))
    }

    @Test(expected = InsufficientPrivilegesException::class)
    fun testNonAdminUserCanNotAssignRoles() {
        createTestUser("user", "pass")
        loginUser("user", "pass")

        val roles = roleService.listRoles().map(Role::id)
        userService.registerUser(UserRegistrationRequest("fakeadmin", "fakeadmin@example.com", "12342", roles))
    }

    @Test(expected = InsufficientPrivilegesException::class)
    fun testNewUserCreationCanNotAssignRoles() {
        val roles = roleService.listRoles().map(Role::id)
        userService.registerUser(UserRegistrationRequest("fakeadmin", "fakeadmin@example.com", "12342", roles))
    }

    @Test(expected = InsufficientPrivilegesException::class)
    fun testNonAdminUserCanNotChangeOtherUser() {
        val existingUser = userService.getUser("1@example.com")

        createTestUser("user", "pass")
        loginUser("user", "pass")


        userService.updateUser(existingUser.id, UserUpdateRequest("new admin name", "qqq", null))
    }

    @Test(expected = InsufficientPrivilegesException::class)
    fun testNonAdminUserCanNotChangeOwnRoles() {
        val user = createTestUser("user", "pass")
        loginUser("user", "pass")

        userService.updateUser(user.id, UserUpdateRequest(null, null, roleService.listRoles().map(Role::id)))
    }

    @WithMockUser("1@example.com")
    @Test
    fun testBanUserSecondTimeAfterUnban() {
        var user = userService.registerUser(UserRegistrationRequest(
                "userForBan",
                "userForBan@example.com",
                "12345",
                emptyList()
        ))

        userService.banUser(user.id, "Ban number 1")
        user = userService.getUser(user.id)
        Assert.assertEquals(UserStatus.BANNED, user.status)

        userService.unbanUser(user.id)
        user = userService.getUser(user.id)
        Assert.assertNull(user.banInfo)
        Assert.assertEquals(UserStatus.ACTIVE, user.status)

        userService.banUser(user.id, "Ban number 2")
        user = userService.getUser(user.id)
        Assert.assertNotNull(user.banInfo)
        Assert.assertEquals(UserStatus.BANNED, user.status)
    }

    @WithMockUser("1@example.com")
    @Test
    fun activateUser() {
        Assert.assertEquals(0, userActivationTokenRepository.count())
        var user = userService.registerUser(UserRegistrationRequest(
                "user",
                "user@example.com",
                "12345",
                emptyList()
        ))
        Assert.assertEquals(UserStatus.REGISTERED, user.status)
        Assert.assertEquals(1, userActivationTokenRepository.count())

        val assertThrows = Assert.assertThrows(EntityDoesNotExistException::class.java) {
            userService.activateUser("wrong token")
        }
        Assert.assertEquals("Token not found", assertThrows.message)

        val token = userActivationTokenRepository.findAll()[0].token
        userService.activateUser(token)
        user = userService.getUser(user.id)
        Assert.assertEquals(UserStatus.ACTIVE, user.status)
        Assert.assertEquals(0, userActivationTokenRepository.count())
    }

    @Test
    fun restorePassword() {
        val email = "1@example.com"
        val oldPassword = "12345"
        val newPassword = "abcdef"

        //old password is valid, new one - not
        var check = userService.checkLogin(LoginRequest(email, oldPassword))
        Assert.assertNotNull(check)
        check = userService.checkLogin(LoginRequest(email, newPassword))
        Assert.assertNull(check)

        val token = userService.createPasswordRestoreToken(email)
        Assert.assertEquals(1, passwordRestoreTokenRepository.count())

        userService.restorePassword(token, newPassword)
        //token deleted after usage
        Assert.assertEquals(0, passwordRestoreTokenRepository.count())

        //password updated
        check = userService.checkLogin(LoginRequest(email, newPassword))
        Assert.assertNotNull(check)
        check = userService.checkLogin(LoginRequest(email, oldPassword))
        Assert.assertNull(check)
    }

    @Test
    fun restorePasswordAttemptWithWrongToken() {
        val assertThrows = Assert.assertThrows(EntityDoesNotExistException::class.java) {
            userService.restorePassword("wrong-token", "abcdef")
        }
        Assert.assertEquals("Token not found", assertThrows.message)
    }

    @Test
    fun restorePasswordAttemptWithOldToken() {
        val email = "1@example.com"
        val newPassword = "abcdef"

        val token = "test-token"
        val tok = PasswordRestoreToken(token, Date(0), email)
        passwordRestoreTokenRepository.save(tok)
        Assert.assertEquals(1, passwordRestoreTokenRepository.count())

        val assertThrows = Assert.assertThrows(PasswordRestoreTokenExpiredException::class.java) {
            userService.restorePassword(token, newPassword)
        }
        Assert.assertTrue(assertThrows.message!!.contains(" and already expired"))
        Assert.assertEquals(0, passwordRestoreTokenRepository.count())
    }
}