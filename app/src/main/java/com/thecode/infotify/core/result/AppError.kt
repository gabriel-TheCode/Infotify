package com.thecode.infotify.core.result

/**
 * Every failure the app can surface, named by what the user needs to understand.
 * The UI maps these to messages; it never inspects raw exceptions.
 */
sealed interface AppError {
    /** The device has no usable connection, or the request timed out. */
    data object NoConnection : AppError

    /** The provider's daily quota is exhausted. Retrying now will not help. */
    data object QuotaExceeded : AppError

    /** The API key is missing, malformed, or rejected. A build/config problem. */
    data object InvalidCredentials : AppError

    /** The provider answered, but with a failure status. */
    data class Server(val code: Int) : AppError

    /** Anything not classified above. [cause] is kept for logging, never shown. */
    data class Unexpected(val cause: Throwable?) : AppError
}
