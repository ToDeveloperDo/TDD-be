package io.junseok.todeveloperdo.domains.member.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): Member?

    fun deleteByUsername(username: String)
}
