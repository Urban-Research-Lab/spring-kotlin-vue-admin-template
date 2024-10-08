package ru.itmo.idu.admin.services.jwt

import io.jsonwebtoken.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.itmo.idu.admin.repositories.UserRepository
import java.util.*


@Component
public class JwtProvider {

    private val logger: Logger = LoggerFactory.getLogger(JwtProvider::class.java)

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("\${assm.app.jwtSecret}")
    lateinit var jwtSecret: String

    fun generateJwtToken(username: String): String {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()
    }

    fun validateJwtToken(authToken: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(authToken)
            return true
        }catch (e: SignatureException) {
            logger.error("Invalid JWT signature -> Message: {} ", e)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token -> Message: {}", e)
        } catch (e: ExpiredJwtException) {
            logger.error("Expired JWT token -> Message: {}", e)
        } catch (e: UnsupportedJwtException) {
            logger.error("Unsupported JWT token -> Message: {}", e)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty -> Message: {}", e)
        }

        return false
    }

    fun getUserNameFromJwtToken(token: String): String {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody().getSubject()
    }
}