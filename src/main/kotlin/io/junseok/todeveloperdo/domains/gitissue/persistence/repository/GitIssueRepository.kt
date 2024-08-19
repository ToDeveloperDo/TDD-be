package io.junseok.todeveloperdo.domains.gitissue.persistence.repository

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import org.springframework.data.jpa.repository.JpaRepository

interface GitIssueRepository : JpaRepository<GitIssue,Long> {
    fun findByTodoList(memberTodoList: MemberTodoList): GitIssue
}