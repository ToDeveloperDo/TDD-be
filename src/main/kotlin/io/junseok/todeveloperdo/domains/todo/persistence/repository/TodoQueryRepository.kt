package io.junseok.todeveloperdo.domains.todo.persistence.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.QMemberTodoList.memberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
import org.springframework.stereotype.Repository

@Repository
class TodoQueryRepository(private val jpaQueryFactory: JPAQueryFactory){
    fun findAllByTodoListMonthAndYear(month: Int, year: Int, member: Member): List<TodoCountResponse> {
        return jpaQueryFactory.select(Projections.bean(
            TodoCountResponse::class.java,
            memberTodoList.deadline,
            memberTodoList.count().`as`("count")
            ))
            .from(memberTodoList)
            .where(
                memberTodoList.deadline.month().eq(month),
                memberTodoList.deadline.year().eq(year),
                memberTodoList.todoStatus.eq(TodoStatus.PROCEED),
                memberTodoList.member.eq(member)
            )
            .groupBy(memberTodoList.deadline)
            .fetch()
    }
}