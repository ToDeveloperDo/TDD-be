package io.junseok.todeveloperdo.auth.config

import io.junseok.todeveloperdo.auth.jwt.JwtAccessDeniedHandler
import io.junseok.todeveloperdo.auth.jwt.JwtAuthenticationEntryPoint
import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.springframework.web.filter.CorsFilter

class SecurityConfigUnitTest : FunSpec({
    val corsFilter = mockk<CorsFilter>()
    val tokenProvider = mockk<TokenProvider>()
    val memberValidator = mockk<MemberValidator>()
    val jwtAuthenticationEntryPoint = mockk<JwtAuthenticationEntryPoint>()
    val jwtAccessDeniedHandler = mockk<JwtAccessDeniedHandler>()

    val securityConfig = SecurityConfig(
        corsFilter,
        tokenProvider,
        memberValidator,
        jwtAuthenticationEntryPoint,
        jwtAccessDeniedHandler
    )

    test("passwordEncoder는 BCryptPasswordEncoder 반환해야한다.") {
        val encoder = securityConfig.passwordEncoder()
        encoder shouldNotBe null
    }
})