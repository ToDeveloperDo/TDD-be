package io.junseok.todeveloperdo.oauth.git.service

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val attributes = mutableMapOf<String, Any?>()
        attributes.putAll(oAuth2User.attributes)
        attributes["access_token"] = userRequest.accessToken.tokenValue

        return DefaultOAuth2User(oAuth2User.authorities, attributes, "id")
    }
}
