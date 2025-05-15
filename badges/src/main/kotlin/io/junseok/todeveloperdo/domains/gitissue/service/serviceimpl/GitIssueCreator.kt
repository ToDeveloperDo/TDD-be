package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component

@Component
class GitIssueCreator {
    fun create(member: Member, memberTodoList: MemberTodoList) = GitIssue(
        content = memberTodoList.content,
        memo = memberTodoList.memo!!,
        tag = memberTodoList.tag,
        deadline = memberTodoList.deadline,
        member = member,
        todoList = memberTodoList
    )
}