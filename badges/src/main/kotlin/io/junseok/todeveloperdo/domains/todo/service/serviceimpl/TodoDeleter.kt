package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TodoDeleter(private val todoListRepository: TodoListRepository) {
    @Transactional
    fun delete(todoList: MemberTodoList) =  todoListRepository.delete(todoList)
}