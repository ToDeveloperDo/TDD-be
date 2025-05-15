package io.junseok.todeveloperdo.auth.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import io.junseok.todeveloperdo.oauth.apple.util.AppleJwtUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class TokenProvider(
    private val memberRepository: MemberRepository,
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.token-validity-in-seconds}") private val tokenValidityInSeconds: Long,
    private val appleClient: AppleClient
) : InitializingBean {
    private val tokenValidityInMilliseconds = tokenValidityInSeconds * 1000
    private var key: Key? = null
    private val log: Logger = LoggerFactory.getLogger(TokenProvider::class.java)

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val keyBytes = Decoders.BASE64.decode(secret)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createToken(authentication: Authentication): String {
        val authorities = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))

        val now = Date().time
        val validity = Date(now + tokenValidityInMilliseconds)

        return Jwts.builder()
            .setSubject(authentication.name)
            .setIssuer("TDD")
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact()
    }

    fun getAuthentication(token: String?): Authentication {
        val claims = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        val authorities: Collection<GrantedAuthority> =
            Arrays.stream(
                claims[AUTHORITIES_KEY].toString().split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray())
                .map { role: String? ->
                    SimpleGrantedAuthority(
                        role
                    )
                }
                .collect(Collectors.toList())

        val principal = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateAppleToken(token: String, type: String): Boolean {
        return try {
            when (type) {
                "REFRESH" -> {
                    val applePublicKeys = appleClient.getApplePublicKeys().keys
                    AppleJwtUtil.decodeAndVerify(token, applePublicKeys)
                    true
                }

                else -> {
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                    true
                }
            }
        } catch (e: ExpiredJwtException) {
            when (type) {
                "REFRESH" -> {
                    if (memberRepository.existsByAppleRefreshToken(token)) {
                        memberRepository.deleteByAppleRefreshToken(token)
                    } else {
                        log.info("No refresh token found to delete.")
                    }
                }
            }
            throw ToDeveloperDoException { ErrorCode.EXPIRED_JWT }

        } catch (e: Exception) {
            log.error(e.message)
            false
        }

    }

    companion object {
        const val AUTHORITIES_KEY = "auth"
    }
}