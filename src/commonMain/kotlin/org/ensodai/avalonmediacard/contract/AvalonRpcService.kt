package org.ensodai.avalonmediacard.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.plugins.MediaStream

/**
 * Главный RPC-контракт между Compose Wasm клиентом и Ktor бэкенд-ядром.
 */
@Rpc
interface AvalonRpcService {
    /**
     * Получить список элементов бокового меню (генерируется на основе активных плагинов).
     */
    suspend fun getSidebarItems(): List<SidebarItem>

    /**
     * Подписаться на поток структуры экрана по строго типизированному объекту Screen.
     */
    fun streamScreen(screen: Screen): Flow<List<UiComponent>>

    /**
     * Поиск видеопотоков для медиафайла.
     * Стримит результаты обратно на клиент по мере их поступления от плагинов.
     */
    fun streamVideoSources(mediaId: String, season: Int? = null, episode: Int? = null): Flow<MediaStream>

    /**
     * Отправка действия (клика по кнопке, навигации) с клиента на бэкенд для обработки плагинами.
     */
    suspend fun handleAction(action: UiAction): UiActionResponse

    /**
     * Загрузить файл плагина на сервер.
     */
    suspend fun uploadPlugin(fileName: String, fileContent: ByteArray): Boolean

    /**
     * Получить настройки виджетов из БД сервера.
     */
    suspend fun getWidgetSettings(): List<WidgetSettingsData>

    /**
     * Сохранить настройки макета виджетов в БД сервера.
     */
    suspend fun saveWidgetLayout(settings: List<WidgetSettingsData>): Boolean
}
