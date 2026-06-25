package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object Integrations : Screen

    @Serializable
    data class PluginHome(val pluginId: String) : Screen

    @Serializable
    data class Details(val mediaId: String, val catalogId: String) : Screen

    @Serializable
    data class Person(
        val personId: String,
        val catalogId: String,
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
        val mediaId: String,
        val catalogId: String,
        val listType: String,
        val title: String
    ) : Screen
}
