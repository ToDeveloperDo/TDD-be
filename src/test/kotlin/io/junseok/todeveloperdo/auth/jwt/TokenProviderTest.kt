package io.junseok.todeveloperdo.auth.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.junseok.todeveloperdo.auth.jwt.TokenProvider.Companion.AUTHORITIES_KEY
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode.EXPIRED_JWT
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import io.junseok.todeveloperdo.oauth.apple.service.createApplePublicKey
import io.junseok.todeveloperdo.oauth.apple.util.AppleJwtUtil
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.instanceOf
import io.mockk.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import java.security.Key
import java.util.*

class TokenProviderTest : FunSpec({
    val memberRepository = mockk<MemberRepository>(relaxed = true)
    val appleClient = mockk<AppleClient>(relaxed = true)

    val key = Keys.secretKeyFor(SignatureAlgorithm.HS512)
    val secret = Base64.getEncoder().encodeToString(key.encoded)
    val tokenValidityInSeconds = 86400L
    val tokenProvider = TokenProvider(
        memberRepository,
        secret,
        tokenValidityInSeconds,
        appleClient
    )
    beforeTest {
        tokenProvider.afterPropertiesSet()
    }

    test("afterPropertiesSet()를 호출하면 key가 초기화 되어야한다.") {
        tokenProvider.afterPropertiesSet()

        val keyField = tokenProvider.javaClass.getDeclaredField("key")
        keyField.isAccessible = true
        val result = keyField.get(tokenProvider)

        result shouldNotBe null
    }

    test("createToken()를 호출하면 JWT Token이 정상적으로 생성되어야한다.") {
        val authentication = UsernamePasswordAuthenticationToken(
            "testuser", "password",
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

        val token = tokenProvider.createToken(authentication)

        val parsed = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)

        val body = parsed.body

        body.subject shouldBe "testuser"
        body.issuer shouldBe "TDD"
        body[AUTHORITIES_KEY] shouldBe "ROLE_USER"
    }

    test("getAuthentication()를 호출하면 Authentication객체가 반환된다.") {
        val authentication = UsernamePasswordAuthenticationToken(
            "testuser", "password",
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
        val token = tokenProvider.createToken(authentication)

        val result = tokenProvider.getAuthentication(token)

        result shouldBe instanceOf<UsernamePasswordAuthenticationToken>()
        result.principal shouldBe User("testuser", "", listOf(SimpleGrantedAuthority("ROLE_USER")))
        result.authorities.map { it.authority } shouldContainExactly listOf("ROLE_USER")
        result.credentials shouldBe token
    }

    test("type이 REFRESH이고, 토큰이 정상적이면 AppleJwtUtil.decodeAndVerify가 실행된다") {
        val authentication = UsernamePasswordAuthenticationToken(
            "testuser",
            "password",
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
        val token = tokenProvider.createToken(authentication)

        val applePublicKeys = listOf(createApplePublicKey())
        val jwt = mockk<DecodedJWT>()

        mockkObject(AppleJwtUtil)
        every { appleClient.getApplePublicKeys().keys } returns applePublicKeys
        every { AppleJwtUtil.decodeAndVerify(token, applePublicKeys) } returns jwt

        val result = tokenProvider.validateAppleToken(token, "REFRESH")

        result shouldBe true

        verify(exactly = 1) {
            AppleJwtUtil.decodeAndVerify(token, applePublicKeys)
        }
    }

    test("type이 ACCESS이고, 토큰이 유효하면 true를 반환한다") {
        val authentication = UsernamePasswordAuthenticationToken(
            "testuser", "password",
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
        val token = tokenProvider.createToken(authentication)

        val result = tokenProvider.validateAppleToken(token, "ACCESS")

        result shouldBe true
    }

    test("type이 ACCESS이고, 토큰이 잘못되었으면 false를 반환한다") {
        val invalidToken = "this.is.invalid"
        val result = tokenProvider.validateAppleToken(invalidToken, "ACCESS")

        result shouldBe false
    }

    test("type이 REFRESH이고, 토큰이 잘못되었으면 false를 반환한다") {
        val invalidToken = "this.is.invalid"
        val result = tokenProvider.validateAppleToken(invalidToken, "REFRESH")

        result shouldBe false
    }

    test("type이 UNKNOWN이면 ACCESS와 동일하게 else 분기를 탄다") {
        val token = tokenProvider.createToken(
            UsernamePasswordAuthenticationToken(
                "testuser",
                "password",
                listOf(SimpleGrantedAuthority("ROLE_USER"))
            )
        )
        val result = tokenProvider.validateAppleToken(token, "SOMETHING_ELSE")
        result shouldBe true
    }


    test("REFRESH 타입에서 만료된 토큰이 있고 DB에 존재하면 삭제 후 예외 발생") {
        val expiredToken = "expired.invalid.token"

        mockkObject(AppleJwtUtil)
        val applePublicKeys = listOf(createApplePublicKey())

        every { appleClient.getApplePublicKeys().keys } returns applePublicKeys
        every { AppleJwtUtil.decodeAndVerify(any(), any()) } throws ExpiredJwtException(
            null,
            null,
            "expired"
        )

        every { memberRepository.existsByAppleRefreshToken(any()) } returns true
        every { memberRepository.deleteByAppleRefreshToken(any()) } just runs

        throwsWith<ToDeveloperDoException>({
            tokenProvider.validateAppleToken(expiredToken, "REFRESH")
        }) { ex ->
            ex.errorCode shouldBe EXPIRED_JWT
        }
    }

    test("REFRESH 타입에서 만료된 토큰이 있고 DB에 존재하지 않으면 로그만 남기고 예외 발생") {
        val expiredToken = "expired.invalid.token"

        mockkObject(AppleJwtUtil)
        val applePublicKeys = listOf(createApplePublicKey())

        every { appleClient.getApplePublicKeys().keys } returns applePublicKeys
        every { AppleJwtUtil.decodeAndVerify(any(), any()) } throws ExpiredJwtException(
            null,
            null,
            "expired"
        )

        every { memberRepository.existsByAppleRefreshToken(expiredToken) } returns false

        throwsWith<ToDeveloperDoException>({
            tokenProvider.validateAppleToken(expiredToken, "REFRESH")
        }) { ex ->
            ex.errorCode shouldBe EXPIRED_JWT
        }
    }

    test("getAuthentication()에서 권한 문자열에 빈 값이 포함되면 dropLastWhile이 실행된다") {
        val authStringWithEmpty = "ROLE_USER,"
        val token = Jwts.builder()
            .setSubject("testuser")
            .setIssuer("TDD")
            .claim(AUTHORITIES_KEY, authStringWithEmpty)
            .signWith(tokenProvider.javaClass.getDeclaredField("key").apply {
                isAccessible = true
            }.get(tokenProvider) as Key, SignatureAlgorithm.HS512)
            .setExpiration(Date(System.currentTimeMillis() + 10000))
            .compact()

        val result = tokenProvider.getAuthentication(token)

        result shouldBe instanceOf<UsernamePasswordAuthenticationToken>()
        result.authorities.map { it.authority } shouldContainExactly listOf("ROLE_USER")
    }

    test("REFRESH 타입이고 만료된 토큰이 DB에 존재하지 않으면 로그만 남기고 예외를 던진다") {
        val expiredToken = "expired.invalid.token"

        mockkObject(AppleJwtUtil)
        val applePublicKeys = listOf(createApplePublicKey())

        every { appleClient.getApplePublicKeys().keys } returns applePublicKeys
        every { AppleJwtUtil.decodeAndVerify(any(), any()) } throws ExpiredJwtException(
            null,
            null,
            "expired"
        )

        every { memberRepository.existsByAppleRefreshToken(expiredToken) } returns false

        throwsWith<ToDeveloperDoException>({
            tokenProvider.validateAppleToken(expiredToken, "REFRESH")
        }) { ex ->
            ex.errorCode shouldBe EXPIRED_JWT
        }
    }

    test("ACCESS 타입에서 ExpiredJwtException이 발생하면 else 분기가 실행된다") {
        val expiredToken = Jwts.builder()
            .setSubject("testuser")
            .setIssuer("TDD")
            .claim(AUTHORITIES_KEY, "ROLE_USER")
            .setExpiration(Date(System.currentTimeMillis() - 10000))
            .signWith(tokenProvider.javaClass.getDeclaredField("key").apply {
                isAccessible = true
            }.get(tokenProvider) as Key, SignatureAlgorithm.HS512)
            .compact()

        throwsWith<ToDeveloperDoException>({
            tokenProvider.validateAppleToken(expiredToken, "ACCESS")
        }) {
            it.errorCode shouldBe EXPIRED_JWT
        }
    }

})
