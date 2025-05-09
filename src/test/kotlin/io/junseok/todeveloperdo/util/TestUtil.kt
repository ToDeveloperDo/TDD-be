package io.junseok.todeveloperdo.util

import io.kotest.assertions.throwables.shouldThrow

inline fun<reified T: Throwable> throwsWith(
    block: () -> Unit,
    crossinline assertions: (T) ->Unit
){
    val exception = shouldThrow<T> { block() }
    assertions(exception)
}