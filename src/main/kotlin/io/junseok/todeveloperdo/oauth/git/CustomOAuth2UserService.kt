package io.junseok.todeveloperdo.oauth.git

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    private val logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        logger.info("coming accessToken !!")
        val oAuth2User = super.loadUser(userRequest)
        val attributes = mutableMapOf<String, Any?>()
        attributes.putAll(oAuth2User.attributes)
        attributes["access_token"] = userRequest.accessToken.tokenValue

        logger.info("OAuth2User attributes: $attributes")

        return DefaultOAuth2User(oAuth2User.authorities, attributes, "id")
    }
}
