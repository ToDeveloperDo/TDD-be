package io.junseok.todeveloperdo.domains.memberfriend.persistence.entity

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import javax.persistence.*

@Entity
@Table(name = "member_friend")
class MemberFriend(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_friend_id")
    var memberFriendId: Long? = null,

    @Column(name = "friend_username")
    val friendUsername: String,

    @Column(name = "friend_git_url")
    val friendGitUrl: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member
) {
}