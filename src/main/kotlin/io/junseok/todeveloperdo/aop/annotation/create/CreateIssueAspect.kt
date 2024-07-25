package io.junseok.todeveloperdo.aop.annotation.create

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class CreateIssueAspect (
    private val eventProcessor: EventProcessor,
    private val memberReader: MemberReader
){
    @Around("@annotation(CreateEvent)")
    fun aroundCreateIssue(joinPoint: ProceedingJoinPoint): Any? {
        val args = joinPoint.args
        val username = args[1] as String
        val member = memberReader.getMember(username)
        val todoRequest = args[0] as TodoRequest

        val issueEventRequest = eventProcessor.createIssue(member, todoRequest)

        // 새로운 args 배열 생성 및 issueEventRequest 추가
        val newArgs = args.copyOf()
        newArgs[2] = issueEventRequest

        // 원래 메소드 호출
        return joinPoint.proceed(newArgs)
    }
}