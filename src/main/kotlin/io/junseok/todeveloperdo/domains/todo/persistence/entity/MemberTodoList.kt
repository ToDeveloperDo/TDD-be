package io.junseok.todeveloperdo.domains.todo.persistence.entity

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "member_todo_list")
class MemberTodoList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_list_id")
    var todoListId: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "memo")
    var memo: String?="",

    @Column(name = "tag")
    var tag: String,

    @Column(name = "deadline")
    var deadline: LocalDateTime,

    @Enumerated(EnumType.STRING)
    var todoStatus: TodoStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member
)
