package org.ensodai.avalonmediacard.contract.plugins

fun calculateTargetCursor(streams: List<MediaStream>): StreamCursor? {
    val mapped = streams.filter { it.isMapped }
    if (mapped.isEmpty()) return null

    val recent = mapped.filter { it.isWatched || it.watchedProgressSeconds != null }
        .maxWithOrNull(
            compareBy<MediaStream> { it.lastWatchedAtEpochMs ?: 0L }
                .thenBy { it.seasonNumber ?: 0 }
                .thenBy { it.episodeNumber ?: 0 }
        )

    if (recent != null) {
        val s = recent.seasonNumber ?: 1
        val e = recent.episodeNumber ?: 1

        val duration = recent.durationSeconds?.toLong() ?: Long.MAX_VALUE
        val progress = recent.watchedProgressSeconds ?: 0L
        val hasActiveProgress = progress > 0L && progress < (duration * 0.95).toLong()

        if (hasActiveProgress || !recent.isWatched) {
            return StreamCursor(s, e, recent.watchedProgressSeconds, recent.episodeName)
        } else {
            val nextEp = mapped.find { it.seasonNumber == s && it.episodeNumber == e + 1 }
                ?: mapped.find { it.seasonNumber == s + 1 && it.episodeNumber == 1 }
            if (nextEp != null) {
                return StreamCursor(nextEp.seasonNumber ?: s, nextEp.episodeNumber ?: (e + 1), null, nextEp.episodeName)
            } else {
                return StreamCursor(s, e, recent.watchedProgressSeconds, recent.episodeName)
            }
        }
    }

    val first = mapped.first()
    return StreamCursor(
        first.seasonNumber ?: 1,
        first.episodeNumber ?: 1,
        first.watchedProgressSeconds,
        first.episodeName
    )
}

fun resolveTargetStream(
    mappedStreams: List<MediaStream>,
    targetSeason: Int? = null,
    targetEpisode: Int? = null
): Pair<MediaStream, StreamCursor?>? {
    if (targetSeason != null && targetEpisode != null) {
        val target = mappedStreams.find { it.seasonNumber == targetSeason && it.episodeNumber == targetEpisode }
            ?: return null
        val cursor = StreamCursor(
            season = targetSeason,
            episode = targetEpisode,
            progressSeconds = target.watchedProgressSeconds,
            episodeName = target.episodeName
        )
        return target to cursor
    }

    val cursor = calculateTargetCursor(mappedStreams)
    val stream = if (cursor != null) {
        mappedStreams.find { it.seasonNumber == cursor.season && it.episodeNumber == cursor.episode }
            ?: mappedStreams.firstOrNull { it.isMapped }
    } else {
        mappedStreams.firstOrNull { it.isMapped }
    }
    return stream?.let { it to cursor }
}
