package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SidebarItem(
    val itemId: String,
    val title: String,
    val iconName: String,
    val screen: Screen? = null
)

