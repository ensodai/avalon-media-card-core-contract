package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import org.ensodai.avalonmediacard.contract.MediaKey
import org.ensodai.avalonmediacard.contract.Screen
import org.ensodai.avalonmediacard.contract.plugins.MediaStream
import kotlin.uuid.Uuid

interface Action

// --- Local Actions (Сайд-эффекты на клиенте) ---
sealed interface LocalAction : Action

@Serializable 
data class ActionNavigate(val screen: Screen) : LocalAction

@Serializable 
data class ActionPlayVideo(
    val url: String, 
    val title: String, 
    val durationSeconds: Double? = null,
    val startPositionSeconds: Long? = null,
    val playlist: List<MediaStream> = emptyList()
) : LocalAction

@Serializable 
data class ActionPreparePlayer(
    val key: MediaKey,
    val title: String
) : LocalAction

@Serializable 
data class ActionPrepareSources(
    val key: MediaKey
) : LocalAction

@Serializable 
data class ActionOpenUrl(val url: String) : LocalAction

@Serializable
data class ActionToggleSources(val expanded: Boolean) : LocalAction

@Serializable
data object ActionClosePlayer : LocalAction


// --- Server Actions (RPC команды на сервер) ---
// Открытый интерфейс для полиморфизма (плагины будут добавлять свои экшены)
interface ServerAction : Action

interface TemplateAction : ServerAction {
    fun withParameter(key: String, value: Any): TemplateAction
}

fun Action.withParameter(key: String, value: Any): Action {
    return if (this is TemplateAction) this.withParameter(key, value) else this
}

interface UserAwareCommand {
    var userId: Uuid?
}

@Serializable
@SerialName("PlayEpisodeCommand")
data class PlayEpisodeCommand(
    val key: MediaKey,
    val seasonNumber: Int = 0,
    val episodeNumber: Int = 0
) : TemplateAction {
    override fun withParameter(key: String, value: Any): TemplateAction {
        return when (key) {
            "seasonNumber" -> copy(seasonNumber = value.toString().toIntOrNull() ?: seasonNumber)
            "episodeNumber" -> copy(episodeNumber = value.toString().toIntOrNull() ?: episodeNumber)
            else -> this
        }
    }
}

@Serializable
data class SaveEpisodeProgressCommand(
    val mediaId: String,
    val season: Int,
    val episode: Int,
    val progressSeconds: Long,
    val durationSeconds: Long,
    val isWatched: Boolean
) : ServerAction

@Serializable
data class ToggleEpisodeWatchedCommand(
    val key: MediaKey,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val isWatched: Boolean
) : ServerAction

@Serializable
data class RateEpisodeCommand(
    val key: MediaKey,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val rating: Int
) : ServerAction
@Serializable
data class FetchMediaSourcesCommand(
    val key: MediaKey,
    val forceRefresh: Boolean = false
) : ServerAction

@Serializable
data class ResolveStreamCommand(
    val stream: MediaStream
) : ServerAction


@Serializable
data class SearchQueryCommand(val query: String) : ServerAction
@Serializable
data class UpdateIntegrationSettingCommand(
    val integrationId: String,
    val key: String,
    val value: String = ""
) : TemplateAction {
    override fun withParameter(key: String, value: Any): TemplateAction {
        return when (key) {
            "key" -> copy(key = value.toString())
            "value" -> copy(value = value.toString())
            else -> this
        }
    }
}

@Serializable
data class RefreshIntegrationsCommand(val service: String) : ServerAction
