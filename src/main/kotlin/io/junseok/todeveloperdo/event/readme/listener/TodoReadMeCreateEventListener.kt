package io.junseok.todeveloperdo.event.readme.listener

import io.junseok.todeveloperdo.event.readme.dto.ReadMeEventRequest
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TodoReadMeCreateEventListener(
    private val readMeProcessor: ReadMeProcessor
) {
    @EventListener
    fun create(readMeEventRequest: ReadMeEventRequest){
        // readMe 파일 작성
        readMeProcessor.generatorReadMe(
            readMeEventRequest.member.gitHubToken!!.toGeneratorBearerToken(),
            readMeEventRequest.member,
            readMeEventRequest.member.gitHubRepo!!
        )
    }
}