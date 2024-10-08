package io.junseok.todeveloperdo.domains.member.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByGitHubUsername(username: String): Boolean
    fun existsByAppleId(appleId: String): Boolean
    fun findByAppleId(appleId: String): Member?
    fun findByGitHubUsername(gitUserName: String): Member?
    fun deleteByAppleId(username: String)
    fun existsByAppleRefreshToken(refreshToken: String): Boolean
    fun deleteByAppleRefreshToken(refreshToken: String)
}
