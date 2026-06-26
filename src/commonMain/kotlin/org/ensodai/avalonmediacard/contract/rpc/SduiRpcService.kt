package org.ensodai.avalonmediacard.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc

@Rpc
interface SduiRpcService {
    fun streamSidebar(): Flow<List<UiComponent>>
    fun streamScreen(screen: Screen): Flow<List<UiComponent>>
    suspend fun getWidgetSettings(): List<WidgetSettingsData>
    suspend fun saveWidgetLayout(settings: List<WidgetSettingsData>): Boolean
}
