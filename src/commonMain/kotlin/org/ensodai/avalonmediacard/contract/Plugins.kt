package org.ensodai.avalonmediacard.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import io.ktor.client.HttpClient

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
 * Возможность плагина интегрировать UI-виджеты в интерфейс ядра.
 */
interface WidgetProvider {
    fun renderWidget(target: WidgetTarget): Flow<List<UiComponent>>
}

/**
 * Возможность плагина предоставлявать видеопотоки (ссылки, стриминг, торренты).
 */
interface StreamProvider {
    fun findStreams(
        mediaId: String,
        season: Int? = null,
        episode: Int? = null
    ): Flow<MediaStream>
}

/**
 * Возможность плагина обрабатывать действия (клики, пагинацию, кастомные экшены).
 */
fun interface ActionHandler {
    suspend fun handleAction(action: UiAction): UiActionResponse
}

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
 * Предоставляет плагинам доступ к фильмам, сериям и оценкам пользователей.
 */
interface UserMovieProvider {
    suspend fun getUserMovies(userId: String): List<UserMovieItem>
    suspend fun updateUserMovie(item: UserMovieItem): Boolean
    suspend fun deleteUserMovie(userId: String, mediaId: String): Boolean
    suspend fun getUserEpisodes(userId: String, mediaId: String): List<UserEpisodeItem>
    suspend fun updateUserEpisode(item: UserEpisodeItem): Boolean
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
    private val _widgetProviders = mutableListOf<WidgetProvider>()
    val widgetProviders: List<WidgetProvider> get() = _widgetProviders

    private val _streamProviders = mutableListOf<StreamProvider>()
    val streamProviders: List<StreamProvider> get() = _streamProviders

    private val _actionHandlers = mutableListOf<ActionHandler>()
    val actionHandlers: List<ActionHandler> get() = _actionHandlers

    private val _callHandlers = mutableListOf<PluginCallHandler>()
    val callHandlers: List<PluginCallHandler> get() = _callHandlers

    suspend fun call(targetPluginId: String, command: String, payloadJson: String): String {
        return dispatcher.call(targetPluginId, command, payloadJson)
    }

    fun registerWidgetProvider(provider: WidgetProvider) {
        _widgetProviders.add(provider)
    }

    fun registerStreamProvider(provider: StreamProvider) {
        _streamProviders.add(provider)
    }

    fun registerActionHandler(handler: ActionHandler) {
        _actionHandlers.add(handler)
    }

    fun registerCallHandler(handler: PluginCallHandler) {
        _callHandlers.add(handler)
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

/**
 * Простейший интерфейс логгера для плагинов.
 */
interface PluginLogger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}
/**
 * Целевые виджеты интеграции компонентов плагина в интерфейс ядра.
 */
@Serializable
sealed interface WidgetTarget {
    @Serializable
    data object HomeWidget : WidgetTarget

    @Serializable
    data class SearchResults(val query: String) : WidgetTarget

    // --- Экран деталей медиа (фильм/сериал) ---
    @Serializable
    data class MediaHeader(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaDescription(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaTrailers(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaCast(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaRecommendations(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaSimilar(val mediaId: String) : WidgetTarget

    // --- Экран деталей персоны (актера/режиссера) ---
    @Serializable
    data class PersonHeader(val personId: String) : WidgetTarget

    @Serializable
    data class PersonImages(val personId: String) : WidgetTarget

    @Serializable
    data class PersonBio(val personId: String) : WidgetTarget

    @Serializable
    data class PersonActingCredits(val personId: String) : WidgetTarget

    @Serializable
    data class PersonDirectingCredits(val personId: String) : WidgetTarget

    @Serializable
    data class MediaList(val movieId: String, val listType: String) : WidgetTarget

    // Резервный кастом для непредвиденных интеграций сторонних плагинов
    @Serializable
    data class Custom(val name: String, val params: Map<String, String> = emptyMap()) : WidgetTarget
}

/**
 * Тип видеопотока.
 */
@Serializable
enum class StreamType {
    DirectUrl,  // Прямая ссылка на файл (mp4, mkv и др.)
    Hls,        // Стриминг HLS (.m3u8)
    Torrent,    // Ссылка на .torrent файл
    Magnet      // Magnet-ссылка
}

/**
 * Результат поиска видеопотока.
 */
@Serializable
data class MediaStream(
    val title: String,
    val url: String,
    val type: StreamType,
    val quality: String? = null,
    val sizeBytes: Long? = null,
    val sourceName: String,
    val seeders: Int? = null,     // Применимо для торрентов
    val leechers: Int? = null     // Применимо для торрентов
)
