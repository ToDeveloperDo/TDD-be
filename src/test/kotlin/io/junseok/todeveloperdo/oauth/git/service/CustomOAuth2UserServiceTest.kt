package io.junseok.todeveloperdo.oauth.git.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

class CustomOAuth2UserServiceTest : FunSpec({

    beforeTest {
        clearAllMocks()
    }

    context("CustomOAuth2UserService의 loadUser 메서드 테스트") {
        test("OAuth2User에 접근 토큰이 추가되어야 함") {
            val authorities = setOf(SimpleGrantedAuthority("ROLE_USER"))
            val userAttributes = mapOf(
                "id" to "1",
                "name" to "테스트유저",
                "email" to "test@example.com"
            )

            val originalOAuth2User = DefaultOAuth2User(authorities, userAttributes, "id")

            val clientRegistration = mockk<ClientRegistration>(relaxed = true)
            val accessToken = mockk<OAuth2AccessToken>(relaxed = true) {
                every { tokenValue } returns "test-access-token"
            }

            val userRequest = mockk<OAuth2UserRequest>(relaxed = true) {
                every { this@mockk.clientRegistration } returns clientRegistration
                every { this@mockk.accessToken } returns accessToken
            }

            val customService = object : CustomOAuth2UserService() {
                fun getOriginalUser(): DefaultOAuth2User {
                    return originalOAuth2User
                }

                override fun loadUser(userRequest: OAuth2UserRequest): DefaultOAuth2User {
                    val user = getOriginalUser()
                    val attributes = mutableMapOf<String, Any?>()
                    attributes.putAll(user.attributes)
                    attributes["access_token"] = userRequest.accessToken.tokenValue

                    return DefaultOAuth2User(user.authorities, attributes, "id")
                }
            }

            val result = customService.loadUser(userRequest)

            result shouldNotBe null
            result.name shouldBe "1"

            result.attributes["access_token"] shouldBe "test-access-token"

            userAttributes.forEach { (key, value) ->
                result.attributes[key] shouldBe value
            }

            result.authorities shouldBe authorities
        }
    }
})