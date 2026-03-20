package com.android.appclock.utils

import android.util.Log

object Logger {

    private const val DEFAULT_TAG = "AppClock"

    enum class Level(val priority: Int) {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR)
    }

    fun v(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(Level.VERBOSE, tag, message, throwable)
    }

    fun d(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(Level.DEBUG, tag, message, throwable)
    }

    fun i(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(Level.INFO, tag, message, throwable)
    }

    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(Level.WARN, tag, message, throwable)
    }

    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(Level.ERROR, tag, message, throwable)
    }

    private fun log(level: Level, tag: String, message: String, throwable: Throwable?) {
        val formattedMessage = formatMessage(message, throwable)

        when (level) {
            Level.VERBOSE -> Log.v(tag, formattedMessage, throwable)
            Level.DEBUG -> Log.d(tag, formattedMessage, throwable)
            Level.INFO -> Log.i(tag, formattedMessage, throwable)
            Level.WARN -> Log.w(tag, formattedMessage, throwable)
            Level.ERROR -> Log.e(tag, formattedMessage, throwable)
        }
    }

    private fun formatMessage(message: String, throwable: Throwable?): String {
        val timestamp = System.currentTimeMillis()
        val threadName = Thread.currentThread().name
        val throwableInfo = throwable?.let { " | Exception: ${it.javaClass.simpleName}" } ?: ""

        return "[$timestamp][$threadName] $message$throwableInfo"
    }
}
