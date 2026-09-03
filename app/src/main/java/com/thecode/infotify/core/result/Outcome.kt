package com.thecode.infotify.core.result

/**
 * Result of an operation that can fail in a way the user must be told about.
 *
 * Deliberately not modelling "loading" or "empty" here: loading belongs to the UI state
 * machine, and an empty list is a successful answer, not an error. Conflating the three
 * was the root cause of the "check your connection" message showing on valid responses.
 */
sealed interface Outcome<out T> {
    data class Success<T>(val data: T) : Outcome<T>
    data class Failure(val error: AppError) : Outcome<Nothing>
}

inline fun <T, R> Outcome<T>.map(transform: (T) -> R): Outcome<R> = when (this) {
    is Outcome.Success -> Outcome.Success(transform(data))
    is Outcome.Failure -> this
}

fun <T> Outcome<T>.getOrNull(): T? = (this as? Outcome.Success)?.data
