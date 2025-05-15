package io.junseok.todeveloperdo.auth.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.AuthenticationException
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationEntryPointTest : FunSpec({
    test("commence should respond with 401 Unauthorized") {
        val entryPoint = JwtAuthenticationEntryPoint()
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val authException = mockk<AuthenticationException>()

        entryPoint.commence(request, response, authException)

        response.status shouldBe HttpServletResponse.SC_UNAUTHORIZED
    }

})
