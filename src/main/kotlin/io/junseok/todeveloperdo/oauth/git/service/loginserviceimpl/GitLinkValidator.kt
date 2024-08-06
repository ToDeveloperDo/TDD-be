package io.junseok.todeveloperdo.oauth.git.service.loginserviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component

@Component
class GitLinkValidator(
    private val memberReader: MemberReader
) {
    fun isGitLink(appleId: String){
        val member = memberReader.getMember(appleId)
        member.gitHubUsername?: throw ToDeveloperDoException{ ErrorCode.NOT_LINK_GITHUB }
    }
}