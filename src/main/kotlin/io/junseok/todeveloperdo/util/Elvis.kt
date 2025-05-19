package io.junseok.todeveloperdo.util

inline fun <T, R> T?.runIfNotNull(block: (T) -> R?): R? =
    if (this != null) block(this) else null
