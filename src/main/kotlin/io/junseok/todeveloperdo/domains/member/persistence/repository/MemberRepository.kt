package io.junseok.todeveloperdo.domains.member.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByGitHubUsername(username: String): Boolean
    fun findByAppleId(appleId: String): Member?
    fun deleteByAppleId(username: String)
    fun existsByGitHubRepo(repoName: String)
    fun existsByAppleRefreshToken(refreshToken: String): Boolean
    fun deleteByAppleRefreshToken(refreshToken: String)
}
