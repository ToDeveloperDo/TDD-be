package io.junseok.todeveloperdo.domains.todo.persistence.repository

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import org.springframework.data.jpa.repository.JpaRepository

interface TodoListRepository : JpaRepository<MemberTodoList,Long>{
}