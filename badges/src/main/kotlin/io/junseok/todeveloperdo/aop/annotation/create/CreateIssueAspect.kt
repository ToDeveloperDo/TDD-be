package io.junseok.todeveloperdo.aop.annotation.create

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.util.TimeProvider
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import java.time.LocalDate

@Aspect
@Component
class CreateIssueAspect(
    private val eventProcessor: EventProcessor,
    private val memberReader: MemberReader,
    private val timeProvider: TimeProvider
) {
    @Around("@annotation(CreateEvent)")
    fun aroundCreateIssue(joinPoint: ProceedingJoinPoint): Any? {
        val args = joinPoint.args
        val username = args[1] as String
        val member = memberReader.getMember(username)
        val todoRequest = args[0] as List<TodoRequest>

        val issueEventRequests =
            todoRequest
                .filter { timeProvider.nowDate() == it.deadline }
                .map { eventProcessor.createIssue(member, it) }

        val newArgs = args.copyOf()
        newArgs[2] =
            if (issueEventRequests.isNotEmpty()) {
                issueEventRequests.first()
            } else null

        return joinPoint.proceed(newArgs)
    }
}