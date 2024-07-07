package io.junseok.todeveloperdo.auth.config

import io.junseok.todeveloperdo.auth.jwt.JwtAccessDeniedHandler
import io.junseok.todeveloperdo.auth.jwt.JwtAuthenticationEntryPoint
import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity //기본적인 웹 보안 활성화
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig(
    private val corsFilter: CorsFilter,
    private val tokenProvider: TokenProvider,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
    ) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity // token을 사용하는 방식이기 때문에 csrf를 disable
            .csrf().disable()

            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler) // enable h2-console

            .and()
            .headers()
            .frameOptions()
            .sameOrigin() // 세션을 사용하지 않기 때문에 STATELESS로 설정

            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .addFilter(corsFilter) //@CrossOrigin(인증이 필요없는 곳에만 허용)
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeHttpRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/favicon.ico").permitAll()
            .antMatchers("/git/login").permitAll()
            .antMatchers("/login/oauth2/code/github").permitAll()
            .anyRequest().authenticated() // 그 외 인증 없이 접근O

            .and()
            .apply(JwtSecurityConfig(tokenProvider)); // Jwt

        return httpSecurity.build()
    }
}