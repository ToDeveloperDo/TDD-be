package io.junseok.todeveloperdo.aop.annotation.update

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class UpdateAspect(
    private val eventProcessor: EventProcessor,
    private val memberReader: MemberReader,
    private val todoReader: TodoReader
) {
    @After("@annotation(UpdateEvent)")
    fun update(joinPoint: JoinPoint) {
        val args = joinPoint.args
        val todoListId = args[0] as Long
        val todoRequest = args[1] as TodoRequest
        val username = args[2] as String
        val member = memberReader.getMember(username)
        val findTodoList = todoReader.findTodoList(todoListId)
        eventProcessor.updateIssueWithReadMe(
            member,
            findTodoList.issueNumber!!,
            todoRequest.toTodoCreate(member)
        )
    }
}