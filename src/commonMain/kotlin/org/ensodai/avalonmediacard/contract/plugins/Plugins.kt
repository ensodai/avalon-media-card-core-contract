package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import io.ktor.client.HttpClient
import org.ensodai.avalonmediacard.contract.model.MediaCatalog
import org.ensodai.avalonmediacard.contract.UiAction
import org.ensodai.avalonmediacard.contract.UiActionCommand

/**
 * Базовый интерфейс для любого плагина в системе Avalon.
 */
interface AvalonPlugin {
    val id: String
    val name: String
    val version: String
    val author: String

    // Декларативные возможности плагина (по умолчанию отсутствуют)
    val widgetProvider: WidgetProvider? get() = null
    val streamProvider: StreamProvider? get() = null
    val actionHandler: ActionHandler? get() = null
    val callHandler: PluginCallHandler? get() = null
    val supportedCommands: List<PluginCommandInfo> get() = emptyList()
    val supportedProviders: List<String> get() = emptyList()

    // Сериализаторы для команд плагина (для типизированной десериализации в ядре)
    val commandSerializers: List<KSerializer<*>> get() = emptyList()

    fun onInitialize(context: PluginContext) {}
    suspend fun onCommand(command: Any) {}
    fun onDestroy() {}
}

@Serializable
data class PluginParameter(
    val name: String,
    val description: String,
    val type: String, // "Int", "String", "Boolean"
    val required: Boolean = true
)

@Serializable
data class PluginCommandInfo(
    val name: String,
    val description: String,
    val parameters: List<PluginParameter> = emptyList()
)

/**
 * Диспетчер для синхронных (suspend) запросов данных между плагинами.
 */
interface PluginCallDispatcher {
    suspend fun call(
        targetPluginId: String,
        command: String,
        payloadJson: String
    ): String
}

/**
 * Обработчик вызовов от других плагинов.
 */
fun interface PluginCallHandler {
    suspend fun onCall(command: String, payloadJson: String): String
}

/**
 * Шина для асинхронной публикации и прослушивания пользовательских действий (экшенов).
 */
interface ActionBus {
    val actions: Flow<UiAction>
    suspend fun post(action: UiAction)
}

/**
 * Контекст, предоставляемый плагину ядром при инициализации.
 */
class PluginContext(
    val pluginDir: String,
    val logger: PluginLogger,
    val httpClient: HttpClient,
    val dispatcher: PluginCallDispatcher,
    val actionBus: ActionBus,
    val catalog: MediaCatalog,
    val userMovies: UserMovieProvider,
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    suspend fun call(targetPluginId: String, command: String, payloadJson: String): String {
        return dispatcher.call(targetPluginId, command, payloadJson)
    }
}

fun <T : Any> T.toAction(pluginId: String, serializer: KSerializer<T>): UiActionCommand {
    val json = Json { ignoreUnknownKeys = true }
    return UiActionCommand(
        pluginId = pluginId,
        commandClass = serializer.descriptor.serialName,
        payloadJson = json.encodeToString(serializer, this)
    )
}

inline fun <reified T : Any> T.toAction(pluginId: String = ""): UiActionCommand =
    this.toAction(pluginId, serializer<T>())

/**
 * Простейший интерфейс логгера для плагинов.
 */
interface PluginLogger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}
