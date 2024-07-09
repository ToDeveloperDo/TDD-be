package io.junseok.todeveloperdo.domains.todo.persistence.repository

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface TodoListRepository : JpaRepository<MemberTodoList,Long>{
    fun findAllByDeadline(deadline: LocalDateTime): List<MemberTodoList>
}