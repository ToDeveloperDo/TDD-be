package io.junseok.todeveloperdo.aop.annotation.create

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class CreateReadMeAspect(
    private val readMeEventProcessor: ReadMeEventProcessor,
    private val memberReader: MemberReader
) {
    @After("@annotation(ReadMeCreate)")
    fun aroundCreateReadMe(joinPoint: JoinPoint){
        val args = joinPoint.args
        val username = args[1] as String
        val member = memberReader.getMember(username)
        readMeEventProcessor.create(member)
    }
}