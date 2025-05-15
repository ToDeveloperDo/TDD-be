package io.junseok.todeveloperdo.aop.annotation.delete

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoDeleter
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.event.EventProcessor
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class DeleteTodoAspect(
    private val eventProcessor: EventProcessor,
    private val memberReader: MemberReader,
    private val todoDeleter: TodoDeleter,
    private val todoReader: TodoReader
) {
    @After("@annotation(DeleteEventHandler)")
    fun afterCloseIssue(joinPoint: JoinPoint) {
        val args = joinPoint.args
        val todoListId = args[0] as Long
        val userName = args[1] as String
        val state = args[2] as String
        val member = memberReader.getMember(userName)
        val todoList = todoReader.findTodoList(todoListId)
        todoDeleter.delete(todoList)
        eventProcessor.closeIssueWithReadMe(member, todoList, state)
    }
}


