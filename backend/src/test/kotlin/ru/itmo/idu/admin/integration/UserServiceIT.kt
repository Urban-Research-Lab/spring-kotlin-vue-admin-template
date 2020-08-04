package ru.itmo.idu.admin.integration

import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.itmo.idu.admin.api_classes.UserRegistrationRequest
import ru.itmo.idu.admin.api_classes.UserUpdateRequest
import ru.itmo.idu.admin.api_classes.dto.CreateRoleRequest
import ru.itmo.idu.admin.services.RoleService
import ru.itmo.idu.admin.services.UserService

class UserServiceIT(

): BaseIntegrationTest() {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var roleService: RoleService

    @Test
    fun testUpdateUserRoles() {
        var user = userService.registerUser(UserRegistrationRequest(
                "userForRoleUpdate",
                "user@example.com",
                "12345",
                emptyList()
        ))

        Assert.assertTrue(user.roles.isEmpty())

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
}