package ru.itmo.idu.admin.model


import jakarta.persistence.*

/**
 * All permissions used on the platform shall be in this enum
 */
enum class Permission {
    MANAGE_USERS,
    MANAGE_ROLES,
    SERVER_ADMIN,
    BAN_USERS
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
        val users: MutableList<User>
) {
    constructor(name: String, permissions: MutableList<Permission>): this(0, name, permissions, mutableListOf<User>())
}