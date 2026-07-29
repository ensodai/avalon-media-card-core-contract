package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.ui.navigation.Screen
import org.ensodai.avalonmediacard.contract.model.SidebarItem
import org.ensodai.avalonmediacard.contract.model.WidgetSettingsData
import org.ensodai.avalonmediacard.contract.slot.GlobalManifest
import org.ensodai.avalonmediacard.contract.slot.ScreenStreamEvent

@Rpc
interface SduiRpcService {
    suspend fun getGlobalManifest(): GlobalManifest
    fun streamSidebar(): Flow<List<SidebarItem>>
    fun streamScreen(screen: Screen): Flow<ScreenStreamEvent>
    suspend fun getWidgetSettings(): List<WidgetSettingsData>
    suspend fun saveWidgetLayout(settings: List<WidgetSettingsData>): Boolean
}
