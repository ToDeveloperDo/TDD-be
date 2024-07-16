package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.domains.gitissue.service.GitIssueService
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.*
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueCreator
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoSearchRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
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
    private val todoUpdater: TodoUpdater,
    private val gitHubIssueProcessor: GitHubIssueProcessor,
    private val gitIssueService: GitIssueService,
    private val gitHubIssueCreator: GitHubIssueCreator,
    private val todoValidator: TodoValidator,
    private val todoDeleter: TodoDeleter,
    private val memberTodoListRepository: TodoListRepository
) {
    fun createTodoList(
        todoRequest: TodoRequest,
        username: String
    ): Long? {
        val member = memberReader.getMember(username)

        //오늘 할 일이 아닌 경우
        if (LocalDate.now() != todoRequest.deadline) {
            gitIssueService.saveGitIssue(todoRequest, member)
            val memberTodoList = todoCreator.generatorTodo(todoRequest, member)
            return todoSaver.saveTodoList(memberTodoList)
        }

        //이슈 템플릿 작성
        val gitHubIssuesRequest =
            gitHubIssueCreator.createIssueTemplate(todoRequest.toTodoCreate(member))

        // 이슈 생성
        val createIssue = gitHubIssueProcessor.createIssue(
            member.gitHubToken,
            member.username,
            member.gitHubRepo!!,
            gitHubIssuesRequest
        )

        val memberTodoList =
            todoCreator.generatorTodo(todoRequest, member, createIssue.number)
        val todoListId = todoSaver.saveTodoList(memberTodoList)

        //리드미 파일 작성
        readMeProcessor.generatorReadMe(
            member.gitHubToken.toGeneratorBearerToken(),
            member,
            member.gitHubRepo!!
        )
        return todoListId
    }

    fun findTodoLists(todoSearchRequest: TodoSearchRequest, username: String): List<TodoResponse> {
        val member = memberReader.getMember(username)
        return todoReader.bringTodoLists(todoSearchRequest.deadline, member)
    }

    fun finishTodoList(todoListId: Long, username: String) {
        val memberTodoList = todoReader.findTodoList(todoListId)
        val member = memberReader.getMember(username)
        todoUpdater.doneTodoList(memberTodoList)
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

    fun modifyTodoList(todoListId: Long, todoRequest: TodoRequest, username: String) {
        val member = memberReader.getMember(username)
        val todoList = todoReader.findTodoList(todoListId)
        todoValidator.isWriter(todoListId, member)
        todoUpdater.update(todoList, todoRequest)
    }

    fun removeTodoList(todoListId: Long, username: String) {
        val todoList = todoReader.findTodoList(todoListId)
        todoDeleter.delete(todoList)
    }

    @Transactional(readOnly = true)
    fun calculateTodoList(
        todoCountRequest: TodoCountRequest,
        username: String
    ): List<TodoCountResponse> {
        val member = memberReader.getMember(username)
        return memberTodoListRepository.findAllByTodoListMonthAndYear(
            todoCountRequest.month,
            todoCountRequest.year,
            member
        )
    }
}