package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList

fun MemberTodoList.doneTodoList() = this.updateTodoStatus()
