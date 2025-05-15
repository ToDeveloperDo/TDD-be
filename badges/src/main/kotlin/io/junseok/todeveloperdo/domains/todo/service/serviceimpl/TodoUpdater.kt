package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TodoUpdater() {
    @Transactional
    fun update(memberTodoList: MemberTodoList, todoRequest: TodoRequest) {
        memberTodoList.updateTodoList(
            todoRequest.content,
            todoRequest.memo!!,
            todoRequest.tag,
            todoRequest.deadline
        )
    }

    @Transactional
    fun doneTodoList(memberTodoList: MemberTodoList) {
        memberTodoList.finishTodoList()
    }

    @Transactional
    fun proceedTodoList(memberTodoList: MemberTodoList) {
        memberTodoList.unFinishTodoList()
    }

    @Transactional
    fun modifyIssueNumber(issueNumber: Int,todoList: MemberTodoList){
        todoList.updateIssueNumber(issueNumber)
    }
}