package io.junseok.todeveloperdo.oauth.git.dto.request

import java.time.LocalDate
import java.util.*

data class FileCommitRequest(
    val message: String,
    val content: String,
    val branch: String,
    val sha: String? = null
){
    companion object{
        fun fileCommitRequestInit(
            owner: String,
            content: String,
            branch: String,
            sha: String?
        ) = FileCommitRequest(
            message = "feat : ${owner}'s ${LocalDate.now()} TodoList😀",
            content = Base64.getEncoder().encodeToString(content.toByteArray()),
            branch = branch,
            sha = sha
        )
    }
}

