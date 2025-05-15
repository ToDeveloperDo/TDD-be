package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component

@Component
class TodoValidator(private val todoListRepository: TodoListRepository) {
    fun isWriter(todoListId: Long, member: Member){
        if(!todoListRepository.existsByTodoListIdAndMember(todoListId,member)){
            throw ToDeveloperDoException {ErrorCode.INVALID_TODOLIST}
        }
    }
}