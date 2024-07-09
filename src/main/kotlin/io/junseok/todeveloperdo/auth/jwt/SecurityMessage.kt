package io.junseok.todeveloperdo.auth.jwt

class SecurityMessage {
    companion object{
        const val SUCCESS_AUTHENTICATION = "Security Context 에 인증 정보를 저장했습니다."
        const val MALFORMED_JWT = "잘못된 JWT 서명입니다."
        const val EXPIRED_JWT = "만료된 JWT 토큰입니다."
        const val UNSUPPORT_JWT = "지원되지 않는 JWT 토큰입니다."
        const val WRONG_JWT = "JWT 토큰이 잘못되었습니다."
    }
}