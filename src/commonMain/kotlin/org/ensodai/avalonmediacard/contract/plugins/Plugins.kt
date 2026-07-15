package org.ensodai.avalonmediacard.contract.plugins

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.modules.SerializersModule
import org.ensodai.avalonmediacard.contract.Screen
import org.ensodai.avalonmediacard.contract.SidebarItem
import org.ensodai.avalonmediacard.contract.model.MediaCatalog
import org.ensodai.avalonmediacard.contract.slot.ActionResult
import org.ensodai.avalonmediacard.contract.slot.ServerAction
import org.ensodai.avalonmediacard.contract.slot.SlotUpdate

import kotlin.reflect.KClass
import kotlin.uuid.Uuid

/**
 * Базовый интерфейс для любого плагина в системе Avalon.
 * 
 * Жизненный цикл:
 * - Инициализируется один раз при старте сервера через [onInitialize].
 * - Регистрирует публичные сервисы через [onBind].
 * - Уничтожается при остановке сервера через [onDestroy].
 */
interface AvalonPlugin {
    val id: String
    val name: String
    val version: String
    val author: String

    fun onInitialize(context: PluginContext) {}
    fun onBind(registry: ServiceRegistry) {}
    fun onDestroy() {}
    fun provideSerializers(): SerializersModule? = null
}

/**
 * Реестр сервисов для межплагинного взаимодействия.
 */
class ServiceRegistry {
    @PublishedApi
    internal val services = mutableMapOf<KClass<*>, Any>()

    fun <T : Any> registerInternalService(klass: KClass<T>, service: T) {
        services[klass] = service
    }

    inline fun <reified T : Any> registerInternalService(service: T) {
        services[T::class] = service
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getInternalService(klass: KClass<T>): T? {
        return services[klass] as? T
    }

    inline fun <reified T : Any> getInternalService(): T? {
        return services[T::class] as? T
    }
}

/**
 * Реестр слотов плагина.
 * Плагин использует его для декларативного описания того, на каких экранах он работает.
 */
class SlotRegistry {
    @PublishedApi
    internal val handlers = mutableMapOf<KClass<out Screen>, (Screen, Uuid?) -> Map<org.ensodai.avalonmediacard.contract.slot.SlotId, Flow<SlotUpdate>>>()

    inline fun <reified T : Screen> onScreen(noinline handler: (T) -> Map<org.ensodai.avalonmediacard.contract.slot.SlotId, Flow<SlotUpdate>>) {
        handlers[T::class] = { screen, _ -> handler(screen as T) }
    }

    inline fun <reified T : Screen> onScreenWithUser(noinline handler: (T, Uuid?) -> Map<org.ensodai.avalonmediacard.contract.slot.SlotId, Flow<SlotUpdate>>) {
        handlers[T::class] = { screen, userId -> handler(screen as T, userId) }
    }

    fun getFlowsForScreen(screen: Screen, userId: Uuid? = null): Map<org.ensodai.avalonmediacard.contract.slot.SlotId, Flow<SlotUpdate>> {
        return handlers[screen::class]?.invoke(screen, userId) ?: emptyMap()
    }
}

/**
 * Реестр команд плагина.
 * Плагин использует его для регистрации обработчиков интерактивных действий от клиента.
 */
class ActionRegistry {
    val handlers = mutableMapOf<KClass<out ServerAction>, suspend (ServerAction, Uuid?) -> ActionResult>()

    inline fun <reified T : ServerAction> bind(noinline handler: suspend (T, Uuid?) -> ActionResult) {
        handlers[T::class] = { action, userId -> handler(action as T, userId) }
    }
}

/**
 * Реестр потоков (видео/аудио) плагина.
 * Плагин использует его для выдачи потоков (видео плеера) для фильма/сериала.
 */
class StreamRegistry {
    private var provider: ((String, Int?, Int?) -> Flow<MediaStream>)? = null
    private var preparer: (suspend (MediaStream) -> String)? = null
    
    fun onMedia(handler: (String, Int?, Int?) -> Flow<MediaStream>) {
        provider = handler
    }

    fun onPrepare(handler: suspend (MediaStream) -> String) {
        preparer = handler
    }
    
    fun getStreams(mediaId: String, season: Int?, episode: Int?): Flow<MediaStream>? = provider?.invoke(mediaId, season, episode)

    suspend fun prepareStream(stream: MediaStream): String? = preparer?.invoke(stream)
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
 * Контекст, предоставляемый плагину ядром при инициализации.
 */
class PluginContext(
    val pluginDir: String,
    val logger: PluginLogger,
    val httpClient: HttpClient,
    val catalog: MediaCatalog,
    val userMovies: UserMovieProvider,
    val userCustomLists: UserCustomListProvider,
    val userEpisodes: UserEpisodeProvider,
    val torrentMappings: TorrentMappingProvider,
    val settings: PluginSettings,
    val userMediaBindings: UserMediaBindingProvider,
    val slots: SlotRegistry = SlotRegistry(),
    val actions: ActionRegistry = ActionRegistry(),
    val streams: StreamRegistry = StreamRegistry(),
    val sidebars: SidebarRegistry = SidebarRegistry(),
    val recommendations: RecommendationEngineRegistrar,
    val telemetry: TelemetryProvider,
    val affinityStore: AffinityVectorStore,
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
)

interface PluginSettings {
    suspend fun getString(key: String): String?
    suspend fun setString(key: String, value: String)
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    suspend fun setBoolean(key: String, value: Boolean)
    fun observeString(key: String, defaultValue: String? = null): Flow<String?>
    fun observeBoolean(key: String, defaultValue: Boolean = false): Flow<Boolean>
}

class SidebarRegistry {
    private var provider: ((Uuid?) -> Flow<List<SidebarItem>>)? = null

    fun onSidebar(handler: (Uuid?) -> Flow<List<SidebarItem>>) {
        provider = handler
    }

    fun getFlow(userId: Uuid?): Flow<List<SidebarItem>>? = provider?.invoke(userId)
}

interface AffinityVectorStore {
    val vectorUpdates: kotlinx.coroutines.flow.Flow<kotlin.uuid.Uuid>
    suspend fun getVector(userId: Uuid): org.ensodai.avalonmediacard.contract.model.AffinityVector?
    suspend fun saveVector(userId: Uuid, vector: org.ensodai.avalonmediacard.contract.model.AffinityVector, eventCount: Int)
    suspend fun getPendingUsers(limit: Int): List<Uuid>
    suspend fun getUserEventCount(userId: Uuid): Int
    suspend fun getCachedEventCount(userId: Uuid): Int?
}
