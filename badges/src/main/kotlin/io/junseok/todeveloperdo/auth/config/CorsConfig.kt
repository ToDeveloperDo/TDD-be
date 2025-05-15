package io.junseok.todeveloperdo.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter{
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true //내 서버가 응답할 때 json을 js에서 처리할 수 있게 할지를 설정하는것
        config.addAllowedOriginPattern("*") //모든 ip의 응답 허용
        config.addAllowedHeader("*") //모든 header 응답 허용
        config.addAllowedMethod("*") //모든 post,get,put,delete,patch의 응답 허용
        config.addAllowedOrigin("http://localhost:8080")
        source.registerCorsConfiguration("/git/login/**", config)
        source.registerCorsConfiguration("/login/oauth2/code/github/**", config)
        source.registerCorsConfiguration("/api/**", config)
        return CorsFilter(source)
    }
}