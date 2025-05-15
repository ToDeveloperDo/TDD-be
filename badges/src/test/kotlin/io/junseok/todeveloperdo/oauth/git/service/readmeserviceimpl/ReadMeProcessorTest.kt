package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.service.createTodoResponse
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.oauth.git.util.toStringDateTime
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.exactly
import io.mockk.*
import java.time.LocalDate
import java.time.LocalDateTime

class ReadMeProcessorTest : BehaviorSpec({
    val todoReader = mockk<TodoReader>()
    val readMeBuilder = mockk<ReadMeBuilder>()
    val readMeCreator = mockk<ReadMeCreator>()

    val readMeProcessor = ReadMeProcessor(
        todoReader,
        readMeBuilder,
        readMeCreator
    )

    val todayTime = LocalDateTime.of(2025, 5, 13, 1, 36)
    val today = LocalDate.of(2025, 5, 13)
    Given("ReadMe에 파일내용을 작성할 때") {
        val todoResponses = listOf(createTodoResponse(4L, "독서하기", today, TodoStatus.PROCEED))
        val member = createMember(1, "appleId", "repo")
        val bearerToken = "bearerToken"
        val todoContent = "todoContent"
        val readMeContent = "readMeContent"

        every {
            todoReader.bringProceedTodoLists(
                todayTime.toStringDateTime(),
                member
            )
        } returns todoResponses
        every { readMeBuilder.buildTodoListString(todoResponses) } returns todoContent
        every { readMeCreator.readMeContentCreate(todoContent) } returns readMeContent
        every {
            readMeCreator.createReadMe(
                bearerToken,
                member.gitHubUsername!!,
                member.gitHubRepo!!,
                readMeContent
            )
        } just runs

        When("generatorReadMe()를 호출하면") {
            readMeProcessor.generatorReadMe(bearerToken, member, member.gitHubRepo!!,todayTime)
            Then("구현체들이 정상적으로 실행이 되어야한다.") {
                verify(exactly = 1) {
                    todoReader.bringProceedTodoLists(
                        todayTime.toStringDateTime(),
                        member
                    )
                }
                verify(exactly = 1) { readMeBuilder.buildTodoListString(todoResponses) }
                verify(exactly = 1) { readMeCreator.readMeContentCreate(todoContent) }
                verify(exactly = 1) {
                    readMeCreator.createReadMe(
                        bearerToken,
                        member.gitHubUsername!!,
                        member.gitHubRepo!!,
                        readMeContent
                    )
                }
            }
        }
    }
})
