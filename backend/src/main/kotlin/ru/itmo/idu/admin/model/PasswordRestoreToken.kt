package ru.itmo.idu.admin.model


import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

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