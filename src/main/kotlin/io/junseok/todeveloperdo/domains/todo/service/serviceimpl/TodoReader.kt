package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoQueryRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.DeadlineTodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.toTodoResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class TodoReader(
    private val todoListRepository: TodoListRepository,
    private val todoQueryRepository: TodoQueryRepository,
) {
    @Transactional(readOnly = true)
    fun bringTodoLists(deadline: LocalDate, member: Member): List<TodoResponse> {
        return todoListRepository.findByDeadlineAndMember(
            deadline,
            member
        ).map { it.toTodoResponse() }
    }

    @Transactional(readOnly = true)
    fun bringProceedTodoLists(deadline: LocalDate, member: Member): List<TodoResponse> {
        return todoListRepository.findByDeadlineAndTodoStatusAndMember(
            deadline,
            TodoStatus.PROCEED,
            member
        ).map { it.toTodoResponse() }
    }

    @Transactional(readOnly = true)
    fun findTodoList(todoListId: Long) = todoListRepository.findByIdOrNull(todoListId)
        ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_TODOLIST }

    @Transactional(readOnly = true)
    fun countByTodoList(
        todoCountRequest: TodoCountRequest,
        member: Member,
    ): List<TodoCountResponse> {
        return todoQueryRepository.findAllByTodoListMonthAndYear(
            todoCountRequest.month,
            todoCountRequest.year,
            member
        )
    }

    @Transactional(readOnly = true)
    fun bringTodoListForWeek(deadline: LocalDate, member: Member): List<DeadlineTodoResponse> {
        val weeksDay = deadline.minusWeeks(1)
        return todoListRepository.findByMemberAndDeadlineBetween(member, weeksDay, deadline)
            .groupBy { it.deadline }
            .map { todo ->
                DeadlineTodoResponse(
                    deadline = todo.key,
                    todoResponse = todo.value.map { it.toTodoResponse() }
                )
            }
    }
    @Transactional(readOnly = true)
    fun findTodoListByTodoStatus(deadline: LocalDate, todoStatus: TodoStatus): List<MemberTodoList> {
        return todoListRepository.findAllByDeadlineAndTodoStatus(deadline, todoStatus)
    }
}