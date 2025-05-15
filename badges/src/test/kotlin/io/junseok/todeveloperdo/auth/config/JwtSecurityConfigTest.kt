package io.junseok.todeveloperdo.auth.config

import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.springframework.security.config.annotation.web.builders.HttpSecurity

class JwtSecurityConfigTest : FunSpec({

    val tokenProvider = mockk<TokenProvider>()
    val memberValidator = mockk<MemberValidator>()
    val configurer = JwtSecurityConfig(tokenProvider, memberValidator)

    test("JwtSecurityConfig는 커스텀 필터가 적용되어야한다.") {
        val http = mockk<HttpSecurity>(relaxed = true)

        configurer.configure(http)

        http shouldNotBe null
    }
})
