package ru.itmo.idu.admin.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty

enum class UserStatus {
    REGISTERED,
    ACTIVE,
    BANNED
}

@Entity
data class BanInfo(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @ManyToOne
        val moderator: User,

        @OneToOne
        val bannedUser: User,

        val timestamp: Long,

        val reason: String
)

@Entity
@Table(name = "\"user\"") // @link https://stackoverflow.com/questions/22256124/cannot-create-a-database-table-named-user-in-postgresql
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long,

        @NotEmpty
        @Column(unique = true)
        var email: String,

        @Column
        var displayName: String?,

        /**
         * BCrypt encrypted password hash + random salt
         */
        @Column(length = 60)
        var password: String,

        @ManyToMany(mappedBy = "users", cascade = [CascadeType.ALL])
        var roles: MutableList<Role>,

        @Column
        var registrationTimestamp: Long,

        val provider: String?,

        @OneToOne(cascade = [CascadeType.ALL], mappedBy = "bannedUser", orphanRemoval = true)
        var banInfo: BanInfo?,

        var status: UserStatus

) {
    constructor(
            email: String,
            password: String,
            roles: MutableList<Role>
    ): this(0L, email, "", password, roles, System.currentTimeMillis(), null, null, UserStatus.REGISTERED)

    constructor() : this("", "", mutableListOf()) {

    }

    override fun toString(): String {
        return "User(id=$id, email='$email', roles=$roles, registrationTimestamp=$registrationTimestamp)"
    }
}