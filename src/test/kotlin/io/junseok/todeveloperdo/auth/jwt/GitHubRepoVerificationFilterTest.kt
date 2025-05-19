package io.junseok.todeveloperdo.auth.jwt

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import javax.servlet.FilterChain

class GitHubRepoVerificationFilterTest : FunSpec({
    val memberValidator = mockk<MemberValidator>()
    val gitHubRepoVerificationFilter = GitHubRepoVerificationFilter(memberValidator)

    val userDetails = mockk<UserDetails>()
    val authentication = mockk<Authentication>()

    beforeTest {
        clearAllMocks()

        every { authentication.isAuthenticated } returns true
        every { authentication.principal } returns userDetails
        every { userDetails.username } returns "testuser"

        SecurityContextHolder.getContext().authentication = authentication
    }

    test("요청 URI가 제외된 경우, memberValidator는 호출되지 않는다") {
        val request = MockHttpServletRequest("GET", "/api/github/check")
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        gitHubRepoVerificationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { memberValidator.isExistRepo(any()) }
    }

    test("요청 URI가 일반 요청이면, memberValidator가 호출된다") {
        val request = MockHttpServletRequest("GET", "/api/github/commit")
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        every { memberValidator.isExistRepo(any()) } returns "repo"

        gitHubRepoVerificationFilter.doFilter(request, response, filterChain)

        verify(exactly = 1) { memberValidator.isExistRepo("testuser") }
    }

    test("인증되지 않은 사용자는 memberValidator가 호출되지 않는다") {
        SecurityContextHolder.getContext().authentication = null

        val request = MockHttpServletRequest("GET", "/api/github/create/repo")
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        gitHubRepoVerificationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { memberValidator.isExistRepo(any()) }
    }

    test("principal이 UserDetails 타입이 아니면 memberValidator는 호출되지 않는다") {
        val nonUserDetailsPrincipal = mockk<Any>()

        every { authentication.isAuthenticated } returns true
        every { authentication.principal } returns nonUserDetailsPrincipal

        SecurityContextHolder.getContext().authentication = authentication

        val request = MockHttpServletRequest("GET", "/api/github/commit")
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        gitHubRepoVerificationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { memberValidator.isExistRepo(any()) }
    }

    test("요청 URI가 /api/github/create/repo 인 경우, memberValidator는 호출되지 않는다") {
        val request = MockHttpServletRequest("GET", "/api/github/create/repo")
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        gitHubRepoVerificationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { memberValidator.isExistRepo(any()) }
    }

    test("authentication이 null이면 memberValidator는 호출되지 않는다") {
        SecurityContextHolder.getContext().authentication = null

        val request = MockHttpServletRequest("GET", "/api/github/commit")
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        gitHubRepoVerificationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { memberValidator.isExistRepo(any()) }
    }

    test("authentication이 존재하지만 인증되지 않았으면 memberValidator는 호출되지 않는다") {
        every { authentication.isAuthenticated } returns false
        SecurityContextHolder.getContext().authentication = authentication

        val request = MockHttpServletRequest("GET", "/api/github/commit")
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        gitHubRepoVerificationFilter.doFilter(request, response, filterChain)

        verify(exactly = 0) { memberValidator.isExistRepo(any()) }
    }

})
