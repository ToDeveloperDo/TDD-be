package io.junseok.todeveloperdo.domains.gitissue.persistence.repository

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface GitIssueRepository : JpaRepository<GitIssue, Long> {
    fun findByTodoList(memberTodoList: MemberTodoList): GitIssue

    @Query("select issue from GitIssue issue where DATE(issue.deadline) = DATE(:deadline)")
    fun findByDeadlineList(@Param(value = "deadline") deadLine: LocalDate): List<GitIssue>
}