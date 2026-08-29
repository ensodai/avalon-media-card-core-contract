package org.ensodai.avalonmediacard.contract.utils

fun String?.toProxyImageUrl(size: String = "w500"): String? {
    if (this.isNullOrBlank()) return null
    if (this.startsWith("/api/media/image/")) return this
    if (this.startsWith("http://") || this.startsWith("https://")) {
        if (this.contains("image.tmdb.org")) {
            val filename = this.substringAfterLast("/").substringBefore("?")
            if (filename.isNotBlank()) return "/api/media/image/$size/$filename"
        }
        return this
    }
    val clean = this.trimStart('/')
    val filename = clean.substringAfterLast("/").substringBefore("?")
    if (filename.isBlank()) return this
    return "/api/media/image/$size/$filename"
}
