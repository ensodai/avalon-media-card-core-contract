package org.ensodai.avalonmediacard.contract.ui.navigation

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.EntityType
import org.ensodai.avalonmediacard.contract.model.MediaKey
import kotlin.uuid.Uuid

@Serializable
sealed interface Screen {
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object Movies : Screen

    @Serializable
    data object TvShows : Screen

    @Serializable
    data object Trends : Screen

    @Serializable
    data object Integrations : Screen

    @Serializable
    data object MyCollection : Screen

    @Serializable
    data class Search(val initialQuery: String = "") : Screen

    @Serializable
    data class CustomList(
        val listId: Uuid,
        val title: String
    ) : Screen

    @Serializable
    data class PluginHome(val pluginId: String) : Screen

    @Serializable
    data class MovieDetails(val key: MediaKey) : Screen

    @Serializable
    data class TvShowDetails(val key: MediaKey) : Screen

    @Serializable
    data class Person(
        val key: MediaKey,
        val personName: String
    ) : Screen

    @Serializable
    data class Dynamic(
        val screenId: String,
        val title: String,
        val params: Map<String, String> = emptyMap()
    ) : Screen

    @Serializable
    data class MediaList(
        val key: MediaKey,
        val listType: String,
        val title: String
    ) : Screen

    companion object {
        fun Details(key: MediaKey): Screen {
            return if (key.type == EntityType.TV) {
                TvShowDetails(key)
            } else {
                MovieDetails(key)
            }
        }
    }
}
