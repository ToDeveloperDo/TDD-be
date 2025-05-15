package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
@Component
class ReadMeBuilder {
    fun buildTodoListString(todoList: List<TodoResponse>): String {
        val stringBuilder = StringBuilder()
        todoList.forEach { todo ->
            stringBuilder.append("- ${todo.content}\n")
            stringBuilder.append("  - Memo: ${todo.memo}\n")
            stringBuilder.append("  - Tag: ${todo.tag}\n")
            stringBuilder.append("  - Deadline: ${todo.deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}\n")
            stringBuilder.append("\n")
        }
        return stringBuilder.toString().trim()
    }
}