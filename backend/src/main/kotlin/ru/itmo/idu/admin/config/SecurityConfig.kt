package ru.itmo.idu.admin.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.itmo.idu.admin.services.OAuthSuccessHandler
import ru.itmo.idu.admin.services.jwt.JwtAuthEntryPoint
import ru.itmo.idu.admin.services.jwt.JwtAuthTokenFilter

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    @Value("\${features.oauthEnabled}") val oauthEnabled: Boolean,
    @Value("\${spring.web.cors.mappings.baseOrigins.allowed-origins}") val allowedOrigins: MutableList<String>,
    @Value("\${spring.web.cors.mappings.baseOrigins.allowed-headers}") val allowedHeaders: MutableList<String>,
    @Value("\${spring.web.cors.mappings.baseOrigins.allowed-methods}") val allowedMethods: MutableList<String>,
    @Value("\${spring.web.cors.mappings.baseOrigins.allow-credentials}") val allowedCredentials: Boolean,
    @Value("\${spring.web.cors.mappings.baseOrigins.max-age}") val maxAge: Long
) {

    @Qualifier("userDetailServiceImpl")
    @Autowired
    internal var userDetailsService: UserDetailsService? = null

    @Autowired
    private val unauthorizedHandler: JwtAuthEntryPoint? = null

    @Autowired
    private val oauthSuccessHandler: OAuthSuccessHandler? = null

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationJwtTokenFilter(): JwtAuthTokenFilter {
        return JwtAuthTokenFilter()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = allowedOrigins
        configuration.allowedHeaders = allowedHeaders
        configuration.allowedMethods = allowedMethods
        configuration.allowCredentials = allowedCredentials
        configuration.maxAge = maxAge
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors().and()
            .csrf().disable().authorizeHttpRequests {
                it.requestMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                if (oauthEnabled) {
                    it.and().oauth2Login().successHandler(oauthSuccessHandler)
                }
            }
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}