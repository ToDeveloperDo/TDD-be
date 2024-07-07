package io.junseok.todeveloperdo.exception

class ToDeveloperDoException(
    var errorCode: () -> ErrorCode
) : RuntimeException()