package org.ensodai.avalonmediacard.contract

interface Navigation {
    fun navigateToDashboard()
    fun navigateToPluginHome(pluginId: String)
    fun navigateToDetails(mediaId: String, catalogId: String)
    fun navigateToPerson(personId: String, catalogId: String, personName: String)
    fun navigateToDynamic(screenId: String, title: String, params: Map<String, String> = emptyMap())
    fun navigateToMediaList(mediaId: String, catalogId: String, listType: String, title: String)
    fun navigateToIntegrations()
    fun navigateBack()
}
