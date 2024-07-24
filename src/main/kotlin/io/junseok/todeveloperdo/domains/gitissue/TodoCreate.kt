package io.junseok.todeveloperdo.domains.gitissue

import com.fasterxml.jackson.annotation.JsonFormat
import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import java.time.LocalDate

data class TodoCreate(
    var issueId: Long?=null,
    val content:String,
    val memo:String?="",
    val tag: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val deadline: LocalDate,
    val member: Member
)

fun GitIssue.toTodoCreate() = TodoCreate(
    issueId= this.issueId,
    content = this.content,
    memo = this.memo,
    tag = this.tag,
    deadline = this.deadline,
    member = this.member
)

fun TodoCreate.toCreateIssueTemplate() = GitHubIssuesRequest(
        title = "${this.deadline} / ${this.content}",
        body = """
                   TODO : ${this.content}
                   MEMO : ${this.memo}
                   TAG : ${this.tag}
                """.trimIndent(),
        assignees = listOf(this.member.username)
    )