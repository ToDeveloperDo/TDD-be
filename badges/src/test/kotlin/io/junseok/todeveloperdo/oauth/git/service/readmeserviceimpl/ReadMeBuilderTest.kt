package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.domains.memberfriend.service.createTodoResponse
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import java.time.LocalDate

class ReadMeBuilderTest : FunSpec({
    val readMeBuilder = ReadMeBuilder()

    test("Todo 리스트를 마크다운 형식의 문자열로 변환해야 한다") {
        val todoList = listOf(
            createTodoResponse(
                id = 1L,
                content = "TDD 작성하기",
                deadline = LocalDate.of(2025, 5, 13),
                status = TodoStatus.PROCEED,
            )
        )

        val result = readMeBuilder.buildTodoListString(todoList)

        result shouldContain todoList[0].content
        result shouldContain todoList[0].memo!!
        result shouldContain todoList[0].tag
        result shouldEndWith("2025-05-13")
    }
})
