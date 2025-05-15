package io.junseok.todeveloperdo.aop.annotation.create

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class CreateReadMeAspect(
    private val readMeEventProcessor: ReadMeEventProcessor,
    private val memberReader: MemberReader
) {
    @Around("@annotation(ReadMeCreate)")
    fun aroundCreateReadMe(joinPoint: ProceedingJoinPoint): Any?{
        val result = joinPoint.proceed()
        val args = joinPoint.args
        val username = args[1] as String
        val member = memberReader.getMember(username)
        readMeEventProcessor.create(member)
        return result
    }
}