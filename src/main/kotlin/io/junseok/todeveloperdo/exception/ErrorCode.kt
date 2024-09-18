package io.junseok.todeveloperdo.exception

enum class ErrorCode(
    val status:Int,
    val message: String
) {
    NOT_EXIST_MEMBER(HttpStatusCode.BAD_REQUEST.status, "존재하지 않는 사용자입니다!"),
    NOT_EXIST_TODOLIST(HttpStatusCode.BAD_REQUEST.status, "존재하지 않는 할 일입니다!"),
    NOT_EXIST_ISSUE(HttpStatusCode.BAD_REQUEST.status, "존재하지 않는 이슈입니다!"),
    INVALID_REPO_NAME_GAP(HttpStatusCode.BAD_REQUEST.status,"공백이 존재할 수 없습니다!"),
    INVALID_REPO_NAME_KOREAN(HttpStatusCode.BAD_REQUEST.status,"영어로만 입력해주세요!"),
    NOT_REQUEST_FRIEND(HttpStatusCode.BAD_REQUEST.status,"요청 기록이 없습니다!"),
    NOT_FRIENDSHIP(HttpStatusCode.BAD_REQUEST.status,"친구 관계가 아닙니다!"),
    ALREADY_FRIENDSHIP(HttpStatusCode.BAD_REQUEST.status,"이미 친구입니다!"),
    ALREADY_SEND_FRIEND_REQUEST(HttpStatusCode.BAD_REQUEST.status,"이미 친구 요청을 상대방에게 보냈습니다!"),
    ALREADY_REQUESTED_FRIEND(HttpStatusCode.BAD_REQUEST.status,"이미 친구 요청을 상대방에게 받았습니다!"),
    INVALID_TODOLIST(HttpStatusCode.BAD_REQUEST.status,"본인의 TODOLIST만 수정가능합니다!"),
    EXPIRED_JWT(HttpStatusCode.UNAUTHORIZED.status, "만료된 JWT 토큰입니다!"),
    NOT_LINK_GITHUB(HttpStatusCode.BAD_REQUEST.status,"Github와 연동되지 않은 계정입니다!"),
    NOT_EXIST_REPO(HttpStatusCode.NOT_FOUND.status,"레포지토리가 존재하지 않습니다!"),
    FAILED_TO_GENERATE_ISSUE(HttpStatusCode.BAD_REQUEST.status,"Git Issue생성에 실패했습니다!"),
    NOT_EXIST_BRANCH(HttpStatusCode.BAD_REQUEST.status,"branch가 존재하지 않습니다!")
}
