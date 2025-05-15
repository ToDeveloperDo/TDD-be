package io.junseok.todeveloperdo.domains.member.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByGitHubUsername(username: String): Boolean
    fun existsByAppleId(appleId: String): Boolean
    fun findByAppleId(appleId: String): Member?
    fun findByGitHubUsername(gitUserName: String): Member?
    fun deleteByAppleId(username: String)
    fun existsByAppleRefreshToken(refreshToken: String): Boolean
    fun deleteByAppleRefreshToken(refreshToken: String)

    @Query(
        "SELECT m FROM Member m " +
                "WHERE m.memberId NOT IN " +
                "(SELECT mt.member.memberId FROM MemberTodoList mt " +
                "WHERE DATE(mt.deadline) = DATE(:deadline))"
    )
    fun findMemberNotWithDeadLine(@Param(value = "deadline") deadline: LocalDate):List<Member>
}
