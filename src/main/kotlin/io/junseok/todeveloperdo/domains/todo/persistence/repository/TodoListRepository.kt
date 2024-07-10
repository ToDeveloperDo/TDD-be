package io.junseok.todeveloperdo.domains.todo.persistence.repository

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface TodoListRepository : JpaRepository<MemberTodoList, Long> {
    fun findByDeadlineAndTodoStatus(date: LocalDate,todoStatus: TodoStatus): List<MemberTodoList>
}