package io.junseok.todeveloperdo.auth.jwt

import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtFilter(
    private val tokenProvider: TokenProvider
) : GenericFilterBean() {
    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpServletRequest = request as HttpServletRequest
        val jwt = resolveToken(httpServletRequest)
        if (StringUtils.hasText(jwt) && tokenProvider.validateAppleToken(jwt!!,"ACCESS")) {
            val authentication = tokenProvider.getAppleAuthentication(jwt)
            SecurityContextHolder.getContext().authentication = authentication
            log.info(SUCCESS_AUTHENTICATION)
        }
        chain.doFilter(request, response)
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val SUCCESS_AUTHENTICATION = "Successfully authenticated with Apple JWT"
    }
}