package ru.itmo.idu.admin.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.idu.admin.model.UserActivationToken

interface UserActivationTokenRepository : JpaRepository<UserActivationToken, String> {
}