package com.poojaseva.data.remote

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/** Convert any throwable from the network layer into a user-friendly message. */
fun Throwable.toUserMessage(): String = when (this) {
    is SocketTimeoutException ->
        "The server is taking too long to respond (it may be waking up). Please try again."
    is HttpException -> httpMessage(this)
    is IOException ->
        "Can't reach the server. Check your internet connection and try again."
    else -> message ?: "Something went wrong. Please try again."
}

private fun httpMessage(e: HttpException): String {
    val detail = runCatching {
        e.response()?.errorBody()?.string()?.let { body ->
            // FastAPI errors look like {"detail": "..."}
            Regex("\"detail\"\\s*:\\s*\"([^\"]*)\"").find(body)?.groupValues?.getOrNull(1)
        }
    }.getOrNull()
    return when {
        !detail.isNullOrBlank() -> detail
        e.code() == 401 -> "Please sign in to continue."
        e.code() == 403 -> "You don't have permission to do that."
        e.code() in 500..599 -> "Server error (${e.code()}). Please try again shortly."
        else -> "Request failed (${e.code()})."
    }
}
