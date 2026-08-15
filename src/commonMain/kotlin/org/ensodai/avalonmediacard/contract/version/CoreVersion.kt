package org.ensodai.avalonmediacard.contract.version

object CoreVersion {
    const val VERSION = "0.5.2"
    const val API_LEVEL = 3
    const val PROTOCOL_VERSION = "2.0"
    const val BUILD_DATE = "2026-08-15"

    fun getDisplayVersion(): String = "Avalon Media Card v$VERSION (API v$API_LEVEL, Protocol v$PROTOCOL_VERSION)"
}
