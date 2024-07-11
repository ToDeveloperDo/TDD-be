package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.domains.gitissue.service.GitIssueService
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoCreator
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoSaver
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.doneTodoList
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueCreator
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoSearchRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MemberTodoService(
    private val readMeProcessor: ReadMeProcessor,
    private val todoReader: TodoReader,
    private val memberReader: MemberReader,
    private val todoSaver: TodoSaver,
    private val todoCreator: TodoCreator,
    private val gitHubIssueProcessor: GitHubIssueProcessor,
    private val gitIssueService: GitIssueService,
    private val gitHubIssueCreator: GitHubIssueCreator
) {
    @Transactional
    fun createTodoList(
        todoCreateRequest: TodoCreateRequest,
        username: String
    ): Long? {
        val member = memberReader.getMember(username)

        //오늘 할 일이 아닌 경우
        if (LocalDate.now() != todoCreateRequest.deadline) {
            gitIssueService.saveGitIssue(todoCreateRequest, member)
            val memberTodoList = todoCreator.generatorTodo(todoCreateRequest, member)
            return todoSaver.saveTodoList(memberTodoList)
        }

        //이슈 템플릿 작성
        val gitHubIssuesRequest =
            gitHubIssueCreator.createIssueTemplate(todoCreateRequest.toTodoCreate(member))

        // 이슈 생성
        val createIssue = gitHubIssueProcessor.createIssue(
            member.gitHubToken,
            member.username,
            member.gitHubRepo!!,
            gitHubIssuesRequest
        )

        val memberTodoList =
            todoCreator.generatorTodo(todoCreateRequest, member, createIssue.number)
        val todoListId = todoSaver.saveTodoList(memberTodoList)

        //리드미 파일 작성
        readMeProcessor.generatorReadMe(
            member.gitHubToken.toGeneratorBearerToken(),
            member,
            member.gitHubRepo!!
        )
        return todoListId
    }

    fun findTodoLists(todoSearchRequest: TodoSearchRequest, username: String):List<TodoResponse>{
        val member = memberReader.getMember(username)
        return todoReader.bringTodoLists(todoSearchRequest.deadline,member)
    }

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