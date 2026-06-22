package org.ensodai.avalonmediacard.contract

import kotlinx.rpc.annotations.Rpc

@Rpc
interface UserMovieRpcService {
    suspend fun getUserMovies(userId: String): List<UserMovieItem>
    suspend fun updateUserMovie(item: UserMovieItem): Boolean
    suspend fun deleteUserMovie(userId: String, mediaId: String): Boolean

    suspend fun getUserEpisodes(userId: String, mediaId: String): List<UserEpisodeItem>
    suspend fun updateUserEpisode(item: UserEpisodeItem): Boolean
}
