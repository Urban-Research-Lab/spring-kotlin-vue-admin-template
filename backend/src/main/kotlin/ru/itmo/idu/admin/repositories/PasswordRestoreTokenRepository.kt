package ru.itmo.idu.admin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.itmo.idu.admin.model.PasswordRestoreToken

@Repository
interface PasswordRestoreTokenRepository : JpaRepository<PasswordRestoreToken, String>