package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object Integrations : Screen

    @Serializable
    data object MyCollection : Screen

    @Serializable
    data class Search(val initialQuery: String = "") : Screen

    @Serializable
    data class CustomList(
        val listId: kotlin.uuid.Uuid,
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
