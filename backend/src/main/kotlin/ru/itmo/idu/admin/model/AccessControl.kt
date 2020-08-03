package ru.itmo.idu.admin.model


import javax.persistence.*

/**
 * All permissions used on the platform shall be in this enum
 */
enum class Permission {
    MANAGE_USERS,
    MANAGE_ROLES,
    SERVER_ADMIN
}

/**
 * Role is a named set of permissions
 */
@Entity
data class Role(
        @Id
        @GeneratedValue
        var id: Long,

        @Column(unique = true)
        var name: String,

        @ElementCollection(fetch = FetchType.EAGER)
        var permissions: MutableList<Permission>,

        @ManyToMany
        val users: List<User>
) {
    constructor(name: String, permissions: MutableList<Permission>): this(0, name, permissions, listOf())
}