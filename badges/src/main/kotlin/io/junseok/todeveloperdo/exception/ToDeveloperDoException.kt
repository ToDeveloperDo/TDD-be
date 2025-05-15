package io.junseok.todeveloperdo.exception

class ToDeveloperDoException(
    errorCodeSupplier: () -> ErrorCode
) : RuntimeException() {
    val errorCode: ErrorCode = errorCodeSupplier()
}
