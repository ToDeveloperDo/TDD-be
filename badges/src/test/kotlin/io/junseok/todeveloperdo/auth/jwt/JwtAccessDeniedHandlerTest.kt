package io.junseok.todeveloperdo.auth.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.access.AccessDeniedException
import javax.servlet.http.HttpServletResponse

class JwtAccessDeniedHandlerTest : FunSpec({
    test("handle should respond with 403 Unauthorized") {
        val handler = JwtAccessDeniedHandler()
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val authException = mockk<AccessDeniedException>()

        handler.handle(request, response, authException)

        response.status shouldBe HttpServletResponse.SC_FORBIDDEN
    }


})
