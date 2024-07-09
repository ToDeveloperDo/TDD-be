package io.junseok.todeveloperdo.auth.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.EXPIRED_JWT
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.MALFORMED_JWT
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.UNSUPPORT_JWT
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.WRONG_JWT
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
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.token-validity-in-seconds}")private val tokenValidityInSeconds: Long
) :
    InitializingBean {
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
        val validity = Date(now + this.tokenValidityInMilliseconds)

        return Jwts.builder()
            .setSubject(authentication.name) //Payload에 유저 네임 저장
            .setIssuer("MealMate") //토큰 발급자 iss 지정
            .claim(AUTHORITIES_KEY, authorities) // 정보 저장
            .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret값 세팅
            .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire안함
            .compact()
    }

    // 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
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

     // 토큰의 유효성 검증을 수행
    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            log.error(MALFORMED_JWT)
        } catch (e: MalformedJwtException) {
            log.error(MALFORMED_JWT)
        } catch (e: ExpiredJwtException) {
            log.error(EXPIRED_JWT)
        } catch (e: UnsupportedJwtException) {
            log.error(UNSUPPORT_JWT)
        } catch (e: IllegalArgumentException) {
            log.error(WRONG_JWT)
        }
        return false
    }

    companion object {
        private const val AUTHORITIES_KEY = "auth"
    }
}