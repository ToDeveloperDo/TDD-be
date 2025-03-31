package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GitIssueUpdater(
    private val issueReader: GitIssueReader
) {
    @Transactional
    fun update(member: Member, todoList: MemberTodoList, todoRequest: TodoRequest) {
        val gitIssue = issueReader.findGitIssueByTodoList(todoList)
        gitIssue.update(todoRequest)
    }
}