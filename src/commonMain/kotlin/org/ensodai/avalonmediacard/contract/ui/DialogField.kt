package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
sealed class DialogField {
    abstract val id: String
    abstract val label: String
}

@Serializable
data class DialogFieldCheckbox(
    override val id: String,
    override val label: String,
    val defaultValue: Boolean
) : DialogField()

@Serializable
data class DialogFieldButton(
    override val id: String,
    override val label: String,
    val action: UiAction
) : DialogField()

@Serializable
data class DialogFieldInfoText(
    override val id: String,
    override val label: String,
    val text: String
) : DialogField()
