package ru.itmo.idu.admin.model

import javax.persistence.*
import javax.validation.constraints.NotEmpty


@Entity
@Table(name = "\"user\"") // @link https://stackoverflow.com/questions/22256124/cannot-create-a-database-table-named-user-in-postgresql
data class User(
        @Id
        @GeneratedValue
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

        @ManyToMany(fetch = FetchType.EAGER)
        var roles: MutableList<Role>,

        @Column
        var registrationTimestamp: Long


) {
    constructor(
            email: String,
            password: String,
            roles: MutableList<Role>
    ): this(0L, email, "", password, roles, System.currentTimeMillis())

    override fun toString(): String {
        return "User(id=$id, email='$email', roles=$roles, registrationTimestamp=$registrationTimestamp)"
    }
}