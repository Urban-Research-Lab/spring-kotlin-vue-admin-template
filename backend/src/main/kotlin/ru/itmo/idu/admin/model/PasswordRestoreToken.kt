package ru.itmo.idu.admin.model


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class PasswordRestoreToken(
        @Id
        @Column(length = 140) // fixes 'index column too large' error
        val token: String,
        val createdAt: Date,
        val email: String
) {
    constructor(email: String, token: String) : this(token, Date(), email)
}