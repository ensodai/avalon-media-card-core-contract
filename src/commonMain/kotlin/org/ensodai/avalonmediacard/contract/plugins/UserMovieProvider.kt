package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.ensodai.avalonmediacard.contract.model.MediaKey
import org.ensodai.avalonmediacard.contract.model.UserEpisodeItem
import org.ensodai.avalonmediacard.contract.model.UserMediaFilter
import org.ensodai.avalonmediacard.contract.model.UserMovieItem
import kotlin.uuid.Uuid

/**
 * Предоставляет плагинам доступ к фильмам, сериям и оценкам пользователей.
 */
interface UserMovieProvider {
    suspend fun getUserMovies(userId: Uuid): List<UserMovieItem>
    fun observeUserMovies(userId: Uuid): Flow<List<UserMovieItem>>
    suspend fun getUserMediaKeys(userId: Uuid, filter: UserMediaFilter): List<MediaKey> = emptyList()
    fun observeUserMediaKeys(userId: Uuid, filter: UserMediaFilter): Flow<List<MediaKey>> = flowOf(emptyList())
    suspend fun updateUserMovie(item: UserMovieItem): Boolean
    suspend fun deleteUserMovie(userId: Uuid, mediaId: String): Boolean
    suspend fun getUserEpisodes(userId: Uuid, mediaId: String): List<UserEpisodeItem>
    suspend fun updateUserEpisode(item: UserEpisodeItem): Boolean
    suspend fun notifyUpdate()
}
