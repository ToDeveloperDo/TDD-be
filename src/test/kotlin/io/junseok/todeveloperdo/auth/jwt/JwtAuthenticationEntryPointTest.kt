package io.junseok.todeveloperdo.auth.jwt

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.core.AuthenticationException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationEntryPointTest : FunSpec({
    test("commence should call sendError(401)") {
        val response = mockk<HttpServletResponse>(relaxed = true)
        val request = mockk<HttpServletRequest>()
        val authException = mockk<AuthenticationException>()
        val entryPoint = JwtAuthenticationEntryPoint()

        entryPoint.commence(request, response, authException)

        verify { response.sendError(HttpServletResponse.SC_UNAUTHORIZED) }
    }

    test("commence should not throw when response is null") {
        val request = mockk<HttpServletRequest>()
        val authException = mockk<AuthenticationException>()
        val entryPoint = JwtAuthenticationEntryPoint()

        shouldNotThrow<Exception> {
            entryPoint.commence(request, null, authException)
        }
    }
})
