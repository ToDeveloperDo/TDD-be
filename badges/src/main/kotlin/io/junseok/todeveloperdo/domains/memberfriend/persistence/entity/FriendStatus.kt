package io.junseok.todeveloperdo.domains.memberfriend.persistence.entity

enum class FriendStatus(val friendStatus: String) {
    FOLLOWING("following"),
    RECEIVE("receive"),
    REQUEST("request"),
    NOT_FRIEND("notFriend")
}