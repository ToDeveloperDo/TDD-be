package io.junseok.todeveloperdo.auth.jwt

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
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
import mu.KotlinLogging
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
    private val appleClient: AppleClient, // AppleClient 주입
    private val memberRepository: MemberRepository,
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.token-validity-in-seconds}")private val tokenValidityInSeconds: Long
) : InitializingBean {
    private val tokenValidityInMilliseconds = tokenValidityInSeconds * 1000
    private var key: Key? = null
    val log = KotlinLogging.logger {}
    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val keyBytes = Decoders.BASE64.decode(secret)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createToken(authentication: Authentication): String {
        val authorities = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))

        // 토큰의 expire 시간을 설정
        val now = Date().time
        val validity = Date(now + tokenValidityInMilliseconds)

        return Jwts.builder()
            .setSubject(authentication.name) //Payload에 유저 네임 저장
            .setIssuer("TDD") //토큰 발급자 iss 지정
            .claim(AUTHORITIES_KEY, authorities) // 정보 저장
            .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
            .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire안함
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

    // 애플 JWT 토큰의 유효성 검증을 수행
    fun validateAppleToken(token: String,type: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        }  catch (e: JWTVerificationException) {
            if(type == "REFRESH"){
                if(memberRepository.existsByAppleRefreshToken(token)){
                    memberRepository.deleteByAppleRefreshToken(token)
                }else {
                    log.info("No refresh token found to delete.")
                }
            }
            throw ToDeveloperDoException{ErrorCode.EXPIRED_JWT}
        }catch (e: ExpiredJwtException) {
            false
        }
    }
    companion object {
        private const val AUTHORITIES_KEY = "auth"
    }
}