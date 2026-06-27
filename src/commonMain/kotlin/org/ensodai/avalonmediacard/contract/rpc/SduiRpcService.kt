package org.ensodai.avalonmediacard.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.slot.SlotUpdate

@Rpc
interface SduiRpcService {
    fun streamSidebar(): Flow<List<SidebarItem>>
    fun streamScreen(screen: Screen): Flow<SlotUpdate>
    suspend fun getWidgetSettings(): List<WidgetSettingsData>
    suspend fun saveWidgetLayout(settings: List<WidgetSettingsData>): Boolean
}
