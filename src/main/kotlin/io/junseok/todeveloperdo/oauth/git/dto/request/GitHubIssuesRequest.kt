package io.junseok.todeveloperdo.oauth.git.dto.request

data class GitHubIssuesRequest(
    val title: String, //제목
    val body: String, // 안에 내용
    val assignees: List<String> = listOf(), // 할당자
    val labels: List<String> = listOf() //우선순위
)