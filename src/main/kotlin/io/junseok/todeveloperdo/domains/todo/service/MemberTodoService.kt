package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoCreator
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoSaver
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.doneTodoList
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoSearchRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberTodoService(
    private val readMeProcessor: ReadMeProcessor,
    private val todoReader: TodoReader,
    private val memberReader: MemberReader,
    private val todoSaver: TodoSaver,
    private val todoCreator: TodoCreator,
    private val gitHubIssueProcessor: GitHubIssueProcessor
) {
    @Transactional
    fun createTodoList(
        todoCreateRequest: TodoCreateRequest,
        username: String
    ): Long? {
        val member = memberReader.getMember(username)

        val gitHubIssuesRequest = GitHubIssuesRequest(
            title = "${todoCreateRequest.deadline} / ${todoCreateRequest.content}",
            body = """
                   TODO : ${todoCreateRequest.content}
                   MEMO : ${todoCreateRequest.memo}
                   TAG : ${todoCreateRequest.tag}
                   SHARE : ${todoCreateRequest.isShare}
                """.trimIndent(),
            assignees = listOf(member.username)
        )

        val createIssue = gitHubIssueProcessor.createIssue(
            member.gitHubToken,
            member.username,
            member.gitHubRepo!!,
            gitHubIssuesRequest
        )

        val memberTodoList = todoCreator.generatorTodo(todoCreateRequest,createIssue.number,member)
        val todoListId = todoSaver.saveTodoList(memberTodoList)

        readMeProcessor.generatorReadMe(
            member.gitHubToken.toGeneratorBearerToken(),
            member,
            member.gitHubRepo!!
        )
        return todoListId
    }
    fun findTodoLists(todoSearchRequest: TodoSearchRequest, username: String) =
        todoReader.bringTodoLists(todoSearchRequest.deadline)

    @Transactional
    fun finishTodoList(todoListId: Long, username: String) {
        val memberTodoList = todoReader.findTodoList(todoListId)
        val member = memberReader.getMember(username)
        memberTodoList.doneTodoList()
        gitHubIssueProcessor.closeIssue(
            member.gitHubToken,
            username,
            member.gitHubRepo!!,
            memberTodoList.issueNumber!!
        )
        readMeProcessor.generatorReadMe(
            member.gitHubToken.toGeneratorBearerToken(),
            member,
            member.gitHubRepo!!
        )
    }

}