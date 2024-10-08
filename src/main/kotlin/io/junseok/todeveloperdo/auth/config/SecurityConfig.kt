package io.junseok.todeveloperdo.auth.config

import io.junseok.todeveloperdo.auth.jwt.JwtAccessDeniedHandler
import io.junseok.todeveloperdo.auth.jwt.JwtAuthenticationEntryPoint
import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig(
    private val corsFilter: CorsFilter,
    private val tokenProvider: TokenProvider,
    private val memberValidator: MemberValidator,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
            .and()
            .headers()
            .frameOptions()
            .sameOrigin()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(corsFilter)
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeHttpRequests()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/favicon.ico").permitAll()
            .antMatchers("/git/login").permitAll()
            .antMatchers("/oauth2/**").permitAll()
            .antMatchers("/login/oauth2/code/github/**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/github/webhook").permitAll()  // POST 요청은 허용
            .antMatchers("/api/github/webhook").denyAll()
            .anyRequest().permitAll()
            .and()
            .apply(JwtSecurityConfig(tokenProvider,memberValidator))

        return httpSecurity.build()
    }
}
