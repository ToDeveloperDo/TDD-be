package io.junseok.todeveloperdo.exception

enum class HttpStatusCode(val status:Int) {
    CREATED(201),
    OK(200),
    CONFLICT(409),
    NOT_FOUND(404),
    BAD_REQUEST(400),
    UNAUTHORIZED(401)
}
