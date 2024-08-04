package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component

@Component
class GitHubRepoValidator(
    private val memberReader: MemberReader,
) {
    fun isExistRepo(appleId: String){
        val member = memberReader.getMember(appleId)
        if(member.gitHubRepo==null)
            throw ToDeveloperDoException{ErrorCode.NOT_EXIST_REPO}
    }
}