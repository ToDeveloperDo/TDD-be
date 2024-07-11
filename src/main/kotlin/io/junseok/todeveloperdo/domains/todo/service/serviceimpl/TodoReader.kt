package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.toTodoResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class TodoReader(
    private val todoListRepository: TodoListRepository
) {
    @Transactional(readOnly = true)
    fun bringTodoLists(deadline: LocalDate, member: Member): List<TodoResponse> {
        return todoListRepository.findByDeadlineAndTodoStatusAndMember(
            deadline,
            TodoStatus.PROCEED,
            member
        )
            .map { it.toTodoResponse() }
    }

    @Transactional(readOnly = true)
    fun findTodoList(todoListId: Long) = todoListRepository.findByIdOrNull(todoListId)
        ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_TODOLIST }
}