package org.ensodai.avalonmediacard.contract

interface Navigation {
    fun navigateToDashboard()
    fun navigateToPluginHome(pluginId: String)
    fun navigateToDetails(key: MediaKey)
    fun navigateToPerson(key: MediaKey, personName: String)
    fun navigateToDynamic(screenId: String, title: String, params: Map<String, String> = emptyMap())
    fun navigateToMediaList(mediaId: String, catalogId: String, listType: String, title: String)
    fun navigateToIntegrations()
    fun navigateBack()
}
