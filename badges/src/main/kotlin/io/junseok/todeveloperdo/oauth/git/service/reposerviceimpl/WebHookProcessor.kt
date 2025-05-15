package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberUpdater
import io.junseok.todeveloperdo.oauth.git.service.GitHubService
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.DELETE_REPO_ACTION
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.RENAMED_REPO_ACTION
import org.springframework.stereotype.Component

@Component
class WebHookProcessor(
    private val payloadCreator: PayloadCreator,
    private val memberReader: MemberReader,
    private val memberUpdater: MemberUpdater,
) {
    fun process(payload: Map<String, Any>, event: String) {
        if (event == GitHubService.REPOSITORY) {
            val action = payload["action"] as String

            when (action) {
                RENAMED_REPO_ACTION -> {
                    val payloadResponse = payloadCreator.create(payload)
                    val member = memberReader.findByGitUserName(payloadResponse.username)
                    memberUpdater.updateMemberRepo(payloadResponse.newRepoName, member)
                }
                DELETE_REPO_ACTION -> {
                    val payloadResponse = payloadCreator.create(payload)
                    val member = memberReader.findByGitUserName(payloadResponse.username)
                    memberUpdater.removeMemberRepo(member)
                }
            }
        }
    }

}