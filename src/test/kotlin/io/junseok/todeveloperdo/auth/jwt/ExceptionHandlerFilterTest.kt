package io.junseok.todeveloperdo.auth.jwt

import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.io.IOException
import java.io.PrintWriter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse

class ExceptionHandlerFilterTest : FunSpec({
    val filter = ExceptionHandlerFilter()

    test("ToDeveloperDoException이 발생하면 JSON 응답을 반환한다") {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val mockChain = mockk<FilterChain>()

        every { mockChain.doFilter(request, response) } throws ToDeveloperDoException { ErrorCode.EXPIRED_JWT }

        filter.doFilter(request, response, mockChain)

        response.status shouldBe ErrorCode.EXPIRED_JWT.status
        response.contentType shouldBe "application/json;charset=UTF-8"
        response.contentAsString shouldBe """{"status":401,"message":"만료된 JWT 토큰입니다!"}"""
    }

    test("writer.write()에서 IOException 발생 시 RuntimeException으로 감싸져야 한다") {
        val request = MockHttpServletRequest()
        val response = mockk<HttpServletResponse>()
        val writer = mockk<PrintWriter>()

        every { response.status = any() } just runs
        every { response.contentType = any() } just runs
        every { response.characterEncoding = any() } just runs
        every { response.writer } returns writer
        every { writer.write(any<String>()) } throws IOException("mapper error")

        val chain = mockk<FilterChain> {
            every { doFilter(any(), any()) } throws ToDeveloperDoException { ErrorCode.EXPIRED_JWT }
        }

        shouldThrow<RuntimeException> {
            filter.doFilter(request, response, chain)
        }
    }

    test("예외가 발생하지 않으면 필터 체인이 정상적으로 실행된다") {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>()

        every { chain.doFilter(any(), any()) } just runs

        filter.doFilter(request, response, chain)
    }

})
