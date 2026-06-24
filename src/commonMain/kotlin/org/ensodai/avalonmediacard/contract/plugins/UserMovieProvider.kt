package org.ensodai.avalonmediacard.contract.plugins

import org.ensodai.avalonmediacard.contract.UserEpisodeItem
import org.ensodai.avalonmediacard.contract.UserMovieItem
import kotlin.uuid.Uuid

/**
 * Предоставляет плагинам доступ к фильмам, сериям и оценкам пользователей.
 */
interface UserMovieProvider {
    suspend fun getUserMovies(userId: Uuid): List<UserMovieItem>
    suspend fun updateUserMovie(item: UserMovieItem): Boolean
    suspend fun deleteUserMovie(userId: Uuid, mediaId: String): Boolean
    suspend fun getUserEpisodes(userId: Uuid, mediaId: String): List<UserEpisodeItem>
    suspend fun updateUserEpisode(item: UserEpisodeItem): Boolean
}
