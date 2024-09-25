package io.junseok.todeveloperdo.auth.jwt

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class GitHubRepoVerificationFilter(
    private val memberValidator: MemberValidator,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is UserDetails) {
                val username = principal.username
                if (request.requestURI.equals("/api/github/create/repo")) {
                    // 레포 검증을 수행
                    memberValidator.isExistRepo(username)
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}