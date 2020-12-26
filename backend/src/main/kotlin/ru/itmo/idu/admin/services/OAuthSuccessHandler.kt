package ru.itmo.idu.admin.services


import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import ru.itmo.idu.admin.services.jwt.JwtProvider
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = LoggerFactory.getLogger(OAuthSuccessHandler::class.java)

@Service
class OAuthSuccessHandler(
        @Autowired
        val authorizedClientService: OAuth2AuthorizedClientService,
        @Autowired
        val userService: UserService,
        @Autowired
        val jwtProvider: JwtProvider,

        @Value("\${frontend.location}")
        val redirectUrl: String
): AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        log.info("Logging in OAuth2 user {}", authentication)
        val authentication = authentication as OAuth2AuthenticationToken
        val client: OAuth2AuthorizedClient = authorizedClientService
                .loadAuthorizedClient(
                        authentication.authorizedClientRegistrationId,
                        authentication.name)

        val userInfoEndpointUri = client.clientRegistration
                .providerDetails.userInfoEndpoint.uri
        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            val restTemplate = RestTemplate()
            val headers = HttpHeaders()
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.accessToken
                    .tokenValue)
            val entity: HttpEntity<*> = HttpEntity<Any?>("", headers)
            val userAttributesResponse: ResponseEntity<MutableMap<String, Any>> = restTemplate
                    .exchange<MutableMap<String, Any>>(userInfoEndpointUri, HttpMethod.GET, entity, MutableMap::class.java)
            val userAttributes: Map<String, Any>? = userAttributesResponse.body
            log.info("User Attributes: {}", userAttributes)
            val userDetails = userService.createUserForOAuthLogin(authentication.authorizedClientRegistrationId, userAttributes!!)
            if (!userDetails.isAccountNonLocked) {
                log.warn("OAuth user is banned")
                val user = userService.getUser(userDetails.username)
                val redirectUrl = "$redirectUrl/signin?banned&reason=${user.banInfo?.reason}"
                response.sendRedirect(redirectUrl)
                return
            }

            val jwtToken = jwtProvider.generateJwtToken(userDetails.username)
            val redirectUrl = "$redirectUrl/oauthLogin?jwt_token=$jwtToken"
            response.sendRedirect(redirectUrl)
        } else {
            log.error("User info endpoint url is empty, can not login")
            throw RuntimeException("Error during OAuth redirect, user info endpoint url")
        }

    }
}