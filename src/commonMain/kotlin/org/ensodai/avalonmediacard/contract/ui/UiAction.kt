package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
sealed class UiAction

@Serializable
data class UiActionNavigate(val screen: Screen) : UiAction()

@Serializable
data class UiActionPlayVideo(val title: String, val streamUrl: String) : UiAction()

@Serializable
data class UiActionOpenUrl(val url: String) : UiAction()

@Serializable
data class UiActionSearch(val query: String) : UiAction()

@Serializable
data class UiActionCustom(
    val pluginId: String,
    val actionId: String,
    val payload: Map<String, String> = emptyMap()
) : UiAction()

@Serializable
data class UiActionLoadMore(
    val pluginId: String,
    val widgetId: String,
    val page: Int
) : UiAction()

@Serializable
data class UiActionCommand(
    val pluginId: String,
    val commandClass: String,
    val payloadJson: String
) : UiAction()

@Serializable
data class UiActionSetRating(
    val mediaId: String,
    val rating: Int
) : UiAction()

@Serializable
data class UiActionSetStatus(
    val mediaId: String,
    val status: MediaStatus
) : UiAction()

@Serializable
data class UiActionShowDialog(
    val dialogId: String,
    val title: String,
    val fields: List<DialogField>,
    val submitAction: UiActionCustom
) : UiAction()
