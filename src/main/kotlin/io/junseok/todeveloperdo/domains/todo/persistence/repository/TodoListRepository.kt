package io.junseok.todeveloperdo.domains.todo.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface TodoListRepository : JpaRepository<MemberTodoList, Long> {
    fun findByDeadlineAndMember(
        date: LocalDate,
        member: Member
    ): List<MemberTodoList>

    fun findByDeadlineAndTodoStatusAndMember(
        date: LocalDate,
        todoStatus: TodoStatus,
        member: Member
    ): List<MemberTodoList>

    fun existsByTodoListIdAndMember(todoListId: Long, member: Member): Boolean

}