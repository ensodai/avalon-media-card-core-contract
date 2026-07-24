package org.ensodai.avalonmediacard.contract.utils

fun String?.toProxyImageUrl(size: String = "w500"): String? {
    if (this.isNullOrBlank()) return null
    if (this.startsWith("/api/media/image/")) return this
    val filename = this.substringAfterLast("/")
    if (filename.isBlank()) return this
    return "/api/media/image/$size/$filename"
}
