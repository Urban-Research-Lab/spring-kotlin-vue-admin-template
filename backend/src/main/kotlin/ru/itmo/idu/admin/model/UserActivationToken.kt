package ru.itmo.idu.admin.model


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class UserActivationToken(
        @Id
        @Column(length = 140) // fixes 'index column too large' error
        val token: String,
        val email: String
) {
    constructor(token: String, user: User) : this(token, user.email)
}