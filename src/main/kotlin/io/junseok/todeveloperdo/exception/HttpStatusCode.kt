package io.junseok.todeveloperdo.exception

import lombok.AllArgsConstructor
import lombok.Getter

@AllArgsConstructor
@Getter
enum class HttpStatusCode(val status:Int) {
    CREATED(201),
    OK(200),
    CONFLICT(409),
    BAD_REQUEST(400),
    UNAUTHORIZED(401)
}
