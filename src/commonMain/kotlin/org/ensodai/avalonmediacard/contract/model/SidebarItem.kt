package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class SidebarItem(
    val id: String,
    val title: String,
    val iconName: String
)
