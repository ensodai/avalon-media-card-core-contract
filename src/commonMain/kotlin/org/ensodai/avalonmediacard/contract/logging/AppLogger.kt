package org.ensodai.avalonmediacard.contract.logging

import co.touchlab.kermit.Logger as KermitLogger
import co.touchlab.kermit.Severity

/**
 * Unified Multiplatform Application Logger.
 * Encapsulates the underlying logging engine (Kermit) behind a clean, zero-overhead API.
 */
interface AppLogger {
    val tag: String

    fun v(throwable: Throwable? = null, message: () -> String)
    fun d(throwable: Throwable? = null, message: () -> String)
    fun i(throwable: Throwable? = null, message: () -> String)
    fun w(throwable: Throwable? = null, message: () -> String)
    fun e(throwable: Throwable? = null, message: () -> String)

    fun v(message: String, throwable: Throwable? = null)
    fun d(message: String, throwable: Throwable? = null)
    fun i(message: String, throwable: Throwable? = null)
    fun w(message: String, throwable: Throwable? = null)
    fun e(message: String, throwable: Throwable? = null)

    fun withTag(tag: String): AppLogger
}

/**
 * Kermit implementation of [AppLogger].
 */
class KermitAppLogger(
    override val tag: String = "Avalon",
    private val delegate: KermitLogger = KermitLogger.withTag(tag)
) : AppLogger {

    override fun v(throwable: Throwable?, message: () -> String) {
        if (delegate.config.minSeverity <= Severity.Verbose) {
            delegate.v(throwable = throwable, message = message)
        }
    }

    override fun d(throwable: Throwable?, message: () -> String) {
        if (delegate.config.minSeverity <= Severity.Debug) {
            delegate.d(throwable = throwable, message = message)
        }
    }

    override fun i(throwable: Throwable?, message: () -> String) {
        if (delegate.config.minSeverity <= Severity.Info) {
            delegate.i(throwable = throwable, message = message)
        }
    }

    override fun w(throwable: Throwable?, message: () -> String) {
        if (delegate.config.minSeverity <= Severity.Warn) {
            delegate.w(throwable = throwable, message = message)
        }
    }

    override fun e(throwable: Throwable?, message: () -> String) {
        if (delegate.config.minSeverity <= Severity.Error) {
            delegate.e(throwable = throwable, message = message)
        }
    }

    override fun v(message: String, throwable: Throwable?) {
        if (delegate.config.minSeverity <= Severity.Verbose) {
            delegate.v(throwable = throwable) { message }
        }
    }

    override fun d(message: String, throwable: Throwable?) {
        if (delegate.config.minSeverity <= Severity.Debug) {
            delegate.d(throwable = throwable) { message }
        }
    }

    override fun i(message: String, throwable: Throwable?) {
        if (delegate.config.minSeverity <= Severity.Info) {
            delegate.i(throwable = throwable) { message }
        }
    }

    override fun w(message: String, throwable: Throwable?) {
        if (delegate.config.minSeverity <= Severity.Warn) {
            delegate.w(throwable = throwable) { message }
        }
    }

    override fun e(message: String, throwable: Throwable?) {
        if (delegate.config.minSeverity <= Severity.Error) {
            delegate.e(throwable = throwable) { message }
        }
    }

    override fun withTag(tag: String): AppLogger {
        return KermitAppLogger(tag = tag, delegate = delegate.withTag(tag))
    }
}

/**
 * Global logging entry point for creating named loggers across all multiplatform targets.
 */
object AppLogging {
    private var rootLogger: AppLogger = KermitAppLogger("Avalon")

    fun setRootLogger(logger: AppLogger) {
        rootLogger = logger
    }

    fun logger(tag: String): AppLogger = rootLogger.withTag(tag)

    inline fun <reified T : Any> logger(): AppLogger =
        logger(T::class.simpleName ?: "Anonymous")
}
