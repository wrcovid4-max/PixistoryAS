package com.pixistory.util

import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/** Raw exception text is never shown to people; screens pick a plain sentence from this check. */
object FriendlyError {
    fun isNetwork(error: Throwable?): Boolean {
        var cause = error
        var depth = 0
        while (cause != null && depth < 8) {
            if (cause is UnknownHostException || cause is ConnectException || cause is SocketTimeoutException ||
                cause is NoRouteToHostException || cause is SSLException
            ) return true
            cause = cause.cause
            depth++
        }
        return false
    }
}
