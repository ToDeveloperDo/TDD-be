package io.junseok.todeveloperdo.domains.gitissue.service

import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueCreator
import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueSaver
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GitIssueService(
    private val gitIssueSaver: GitIssueSaver,
    private val gitIssueCreator: GitIssueCreator,
) {
    @Transactional
    fun saveGitIssue(todoRequest: TodoRequest, member: Member, memberTodoList: MemberTodoList) {
        val gitIssue = gitIssueCreator.create(todoRequest, member,memberTodoList)
        gitIssueSaver.save(gitIssue)
    }
}