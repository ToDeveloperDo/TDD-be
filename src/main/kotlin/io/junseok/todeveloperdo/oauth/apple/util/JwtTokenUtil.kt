package io.junseok.todeveloperdo.oauth.apple.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtTokenUtil {

    fun generateToken(user: Map<String, Any>, secret: String): String {
        val algorithm = Algorithm.HMAC256(secret)
        return JWT.create()
            .withSubject(user["sub"] as String)
            .withClaim("email", user["email"] as String)
            .withClaim("name", user["name"] as String)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000)) // 1시간 유효기간
            .sign(algorithm)
    }
}
