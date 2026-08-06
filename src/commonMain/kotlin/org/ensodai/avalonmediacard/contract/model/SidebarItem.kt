package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.ui.navigation.Screen

@Serializable
enum class SidebarItemType {
    MENU_ITEM,
    DIVIDER,
    HEADER
}

@Serializable
data class SidebarItem(
    val itemId: String,
    val title: String? = null,
    val iconName: String? = null,
    val screen: Screen? = null,
    val type: SidebarItemType = SidebarItemType.MENU_ITEM,
    val group: Int = 0, // 0 - Main, 1 - Collection, 2 - Custom Lists
    val order: Int = 0,
    val itemsCount: Int? = null
)
