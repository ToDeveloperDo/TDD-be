package io.junseok.todeveloperdo.presentation.membertodolist.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import io.junseok.todeveloperdo.domains.gitissue.TodoCreate
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import java.time.LocalDate

fun TodoRequest.toTodoCreate(member: Member) = TodoCreate(
    content = this.content,
    memo = this.memo,
    tag = this.tag,
    deadline = this.deadline,
    member = member
)
data class TodoRequest(
    val content:String,
    val memo:String?="",
    val tag: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val deadline: LocalDate
)
