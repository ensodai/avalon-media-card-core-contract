package org.ensodai.avalonmediacard.contract.ui.navigation

import org.ensodai.avalonmediacard.contract.model.MediaKey
import kotlin.uuid.Uuid

interface Navigation {
    fun navigateToDashboard()
    fun navigateToPluginHome(pluginId: String)
    fun navigateToDetails(key: MediaKey)
    fun navigateToPerson(key: MediaKey, personName: String)
    fun navigateToDynamic(screenId: String, title: String, params: Map<String, String> = emptyMap())
    fun navigateToMediaList(key: MediaKey, listType: String, title: String)
    fun navigateToIntegrations()
    fun navigateToSettings()
    fun navigateToAdmin()
    fun navigateToMyCollection()
    fun navigateToCustomList(listId: Uuid, title: String)
    fun navigateToSearch(initialQuery: String)
    fun navigateTo(screen: Screen)
    fun navigateBack()
}
