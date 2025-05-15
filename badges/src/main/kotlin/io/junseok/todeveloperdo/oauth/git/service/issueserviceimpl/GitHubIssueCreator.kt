package io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl

import io.junseok.todeveloperdo.domains.gitissue.TodoCreate
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import org.springframework.stereotype.Component

@Component
class GitHubIssueCreator {
    fun createIssueTemplate(todoCreate: TodoCreate) =
        GitHubIssuesRequest(
            title = "${todoCreate.deadline} / ${todoCreate.content}",
            body = """
                   TODO : ${todoCreate.content}
                   MEMO : ${todoCreate.memo}
                   TAG : ${todoCreate.tag}
                """.trimIndent(),
            assignees = listOf(todoCreate.member.gitHubUsername!!)
        )
}