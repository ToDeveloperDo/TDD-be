package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.oauth.git.util.toStringDateTime
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReadMeProcessor(
    private val todoReader: TodoReader,
    private val readMeBuilder: ReadMeBuilder,
    private val readMeCreator: ReadMeCreator
) {
    /**
     * README 파일내용 작성
     */
    fun generatorReadMe(
        bearerToken: String,
        member: Member,
        repo: String
    ) {
        val findTodoList =
            todoReader.bringProceedTodoLists(LocalDateTime.now().toStringDateTime(), member)
        val todoListContent = readMeBuilder.buildTodoListString(findTodoList)
        val readmeContent = readMeCreator.readMeContentCreate(todoListContent)
        readMeCreator.createReadMe(bearerToken, member.gitHubUsername!!, repo, readmeContent)
    }
}