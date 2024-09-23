package io.junseok.todeveloperdo.auth.config

import io.junseok.todeveloperdo.auth.jwt.ExceptionHandlerFilter
import io.junseok.todeveloperdo.auth.jwt.GitHubRepoVerificationFilter
import io.junseok.todeveloperdo.auth.jwt.JwtFilter
import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtSecurityConfig(
    private val tokenProvider: TokenProvider,
    private val memberValidator: MemberValidator
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain?, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        // security 로직에 JwtFilter 등록
        http
            .addFilterBefore(
                JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterAfter(
                GitHubRepoVerificationFilter(memberValidator),  // GitHub 레포지토리 검증 필터
                JwtFilter::class.java
            )
            .addFilterBefore(
                ExceptionHandlerFilter(),
                JwtFilter::class.java
            )
    }
}