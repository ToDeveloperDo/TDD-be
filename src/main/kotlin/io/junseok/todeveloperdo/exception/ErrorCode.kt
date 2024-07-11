package io.junseok.todeveloperdo.exception

enum class ErrorCode(
    val status:Int,
    val message: String
) {
    NOT_EXIST_MEMBER(HttpStatusCode.BAD_REQUEST.status, "존재하지 않는 사용자입니다!"),
    NOT_EXIST_TODOLIST(HttpStatusCode.BAD_REQUEST.status, "존재하지 않는 할 일입니다!"),
    NOT_EXIST_ISSUE(HttpStatusCode.BAD_REQUEST.status, "존재하지 않는 이슈입니다!"),
    INVALID_REPO_NAME_GAP(HttpStatusCode.BAD_REQUEST.status,"공백이 존재할 수 없습니다!"),
    INVALID_REPO_NAME_KOREAN(HttpStatusCode.BAD_REQUEST.status,"영어로만 입력해주세요!")
}
