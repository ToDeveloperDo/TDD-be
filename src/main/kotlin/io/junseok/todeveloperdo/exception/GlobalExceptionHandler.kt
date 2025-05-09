package io.junseok.todeveloperdo.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ToDeveloperDoException::class)
    fun errorCodeResponseEntity(ex: ToDeveloperDoException): ResponseEntity<ErrorResponseEntity> =
        ex.errorCode.responseEntity()
}
