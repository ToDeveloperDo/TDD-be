package io.junseok.todeveloperdo.auth.jwt

import io.junseok.todeveloperdo.auth.jwt.JwtFilter.Companion.AUTHORIZATION_HEADER
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.FilterChain

class JwtFilterTest : FunSpec({
    val tokenProvider = mockk<TokenProvider>()
    val jwtFilter = JwtFilter(tokenProvider)
    val chain = mockk<FilterChain>(relaxed = true)
    val authentication = mockk<Authentication>(relaxed = true)

    beforeTest {
        SecurityContextHolder.clearContext()
    }

    test("유효한 JWT가 있으면 인증 정보를 설정하고 체인을 실행한다") {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val jwt = "valid.jwt.token"
        request.addHeader("Authorization", "Bearer $jwt")

        every { tokenProvider.validateAppleToken(jwt, "ACCESS") } returns true
        every { tokenProvider.getAuthentication(jwt) } returns authentication

        jwtFilter.doFilter(request, response, chain)

        SecurityContextHolder.getContext().authentication shouldBe authentication
        verify { chain.doFilter(request, response) }
        verify { tokenProvider.validateAppleToken(jwt, "ACCESS") }
        verify { tokenProvider.getAuthentication(jwt) }
    }

    test("validateAppleToken이 false이면 조건문을 통과하지 않는다") {
        val jwt = "fake.jwt.token"
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        request.addHeader("Authorization", "Bearer $jwt")

        every { tokenProvider.validateAppleToken(jwt, "ACCESS") } returns false

        jwtFilter.doFilter(request, response, chain)

        SecurityContextHolder.getContext().authentication shouldBe null
        verify { chain.doFilter(request, response) }
    }

    test("jwt가 null이면 조건문을 통과하지 않는다") {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        jwtFilter.doFilter(request, response, chain)

        SecurityContextHolder.getContext().authentication shouldBe null
        verify { chain.doFilter(request, response) }
    }


    test("Authorization 헤더가 'Bearer '로 시작하면 토큰을 잘 파싱해야 한다") {
        val request = MockHttpServletRequest()
        val jwt = "valid.jwt.token"
        request.addHeader("Authorization", "Bearer $jwt")

        val resolveToken = jwtFilter.resolveToken(request)

        resolveToken shouldBe jwt
    }

    test("Authorization 헤더가 없으면 null을 반환해야 한다") {
        val request = MockHttpServletRequest()
        jwtFilter.resolveToken(request) shouldBe null
    }

    test("Authorization 헤더가 Bearer로 시작하지 않으면 null을 반환해야 한다") {
        val request = MockHttpServletRequest()
        request.addHeader(AUTHORIZATION_HEADER, "Basic abc.def.ghi")
        jwtFilter.resolveToken(request) shouldBe null
    }

    afterTest {
        SecurityContextHolder.clearContext()
    }
})
