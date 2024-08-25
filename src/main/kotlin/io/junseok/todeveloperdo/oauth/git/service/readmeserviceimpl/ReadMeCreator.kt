package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.git.client.GtiHubReadMeClient
import io.junseok.todeveloperdo.oauth.git.dto.request.fileCommitRequestInit
import io.junseok.todeveloperdo.oauth.git.service.GitHubService
import io.junseok.todeveloperdo.oauth.git.util.toStringDateTime
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReadMeCreator(
    private val gitHubReadMeClient: GtiHubReadMeClient
) {
    fun readMeContentCreate(todoListContent: String) = """
            |# 📝${LocalDateTime.now().toStringDateTime()} / TODOLIST📝
            |$todoListContent
        """.trimMargin()

    fun createReadMe(
        token: String,
        owner: String,
        repo: String,
        content: String
    ) {
        val branches = gitHubReadMeClient.getBranches(token, owner, repo)
        val defaultBranch = branches.firstOrNull { it.name == "main" || it.name == "master" }
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_BRANCH }

        val newReadme = gitHubReadMeClient.getFile(token, owner, repo, GitHubService.PATH)

        val fileCommitRequest =
            fileCommitRequestInit(owner, content, defaultBranch.name, newReadme.sha)

        gitHubReadMeClient.createOrUpdateFile(
            token = token,
            owner = owner,
            repo = repo,
            path = "README.md",
            body = fileCommitRequest
        )
    }
}