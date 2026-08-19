package org.ensodai.avalonmediacard.contract.plugins

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.modules.SerializersModule
import org.ensodai.avalonmediacard.contract.model.*
import org.ensodai.avalonmediacard.contract.slot.*
import org.ensodai.avalonmediacard.contract.ui.navigation.Screen
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
    val declarations: Map<KClass<out Screen>, List<SlotId>>
        get() = _declarations

    @PublishedApi
    internal val _declarations = mutableMapOf<KClass<out Screen>, List<SlotId>>()

    @PublishedApi
    internal val handlers = mutableMapOf<KClass<out Screen>, suspend (Screen, Uuid?) -> ScreenSlots>()

    @PublishedApi
    internal val manifestLayoutBuilders = mutableMapOf<KClass<out Screen>, suspend (Uuid?) -> List<LayoutNode>>()

    inline fun <reified T : Screen> declare(vararg slotIds: SlotId) {
        val existing = _declarations[T::class] ?: emptyList()
        _declarations[T::class] = (existing + slotIds).distinct()
    }

    inline fun <reified T : Screen> declare(
        slots: List<SlotId>,
        noinline manifestLayout: suspend (Uuid?) -> List<LayoutNode>
    ) {
        val existing = _declarations[T::class] ?: emptyList()
        _declarations[T::class] = (existing + slots).distinct()
        manifestLayoutBuilders[T::class] = manifestLayout
    }

    fun getManifestLayoutBuilder(screenClass: KClass<out Screen>): (suspend (Uuid?) -> List<LayoutNode>)? {
        return manifestLayoutBuilders[screenClass]
    }

    inline fun <reified T : Screen> onScreen(noinline handler: suspend (T, Uuid?) -> ScreenSlots) {
        handlers[T::class] = { screen, userId -> handler(screen as T, userId) }
    }

    suspend fun getScreenSlots(screen: Screen, userId: Uuid? = null): ScreenSlots? {
        return handlers[screen::class]?.invoke(screen, userId)
    }
}

data class ScreenSlots(
    val layout: List<LayoutNode> = emptyList(),
    val flow: Flow<ScreenStreamEvent>
)

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
class StreamRegistry(
    private val fallbackProvider: (suspend (MediaKey, String, Uuid?) -> List<MediaStream>?)? = null
) {
    private var provider: ((String, Int?, Int?, Uuid?) -> Flow<MediaStream>)? = null
    private var preparer: (suspend (MediaStream, Uuid?) -> MediaStream)? = null
    private var playlistProvider: (suspend (MediaKey, String, Uuid?) -> List<MediaStream>)? = null

    fun onMedia(handler: (String, Int?, Int?, Uuid?) -> Flow<MediaStream>) {
        provider = handler
    }

    fun onPrepare(handler: suspend (MediaStream, Uuid?) -> MediaStream) {
        preparer = handler
    }

    fun onPlaylist(handler: suspend (MediaKey, String, Uuid?) -> List<MediaStream>) {
        playlistProvider = handler
    }

    fun getStreams(mediaId: String, season: Int?, episode: Int?, userId: Uuid?): Flow<MediaStream>? =
        provider?.invoke(mediaId, season, episode, userId)

    suspend fun prepareStream(stream: MediaStream, userId: Uuid?): MediaStream? = preparer?.invoke(stream, userId)

    suspend fun getPlaylist(key: MediaKey, sourceId: String, userId: Uuid?): List<MediaStream>? =
        playlistProvider?.invoke(key, sourceId, userId) ?: fallbackProvider?.invoke(key, sourceId, userId)
}


/**
 * Простейший интерфейс логгера для плагинов.
 */
interface PluginLogger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

enum class IntegrationSettingSource {
    PERSONAL,
    SHARED
}

data class ResolvedIntegrationSetting(
    val value: String,
    val source: IntegrationSettingSource
)

data class ResolvedSearchEngineSetting(
    val url: String,
    val apiKey: String,
    val source: IntegrationSettingSource
)

interface IntegrationSettingsManager {
    suspend fun getTmdbToken(userId: Uuid?): ResolvedIntegrationSetting?
    suspend fun getTorrServerHost(userId: Uuid?): ResolvedIntegrationSetting?
    suspend fun getTorrServerAuth(userId: Uuid?): String? // Basic auth header if present
    suspend fun getTorrServerUseGst(userId: Uuid?): Boolean = false
    suspend fun getProwlarrSettings(userId: Uuid?): ResolvedSearchEngineSetting? = null
    suspend fun getJackettSettings(userId: Uuid?): ResolvedSearchEngineSetting? = null
}

interface UserFeedCacheProvider {
    suspend fun getSections(userId: Uuid, scope: String, language: String = "ru-RU"): List<DynamicSection>?
    suspend fun saveSections(userId: Uuid, scope: String, language: String = "ru-RU", sections: List<DynamicSection>)
    suspend fun invalidateUser(userId: Uuid)
    suspend fun invalidateAll()
}

/**
 * Интерфейс для отправки асинхронных обновлений слотов от плагинов в ядро.
 */
interface SlotUpdater {
    suspend fun emitSlotUpdate(userId: Uuid, key: MediaKey, update: SlotUpdate)
}

object DummySlotUpdater : SlotUpdater {
    override suspend fun emitSlotUpdate(userId: Uuid, key: MediaKey, update: SlotUpdate) {}
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
    val userSettings: UserPluginSettings,
    val integrationManager: IntegrationSettingsManager,
    val userMediaBindings: UserMediaBindingProvider,
    val updater: SlotUpdater = DummySlotUpdater,
    val slots: SlotRegistry = SlotRegistry(),
    val actions: ActionRegistry = ActionRegistry(),
    val streams: StreamRegistry = StreamRegistry(),
    val sidebars: SidebarRegistry = SidebarRegistry(),
    val recommendations: RecommendationEngineRegistrar,
    val telemetry: TelemetryProvider,
    val affinityStore: AffinityVectorStore,
    val genreDictionary: GenreDictionaryProvider,
    val feedCache: UserFeedCacheProvider = object : UserFeedCacheProvider {
        override suspend fun getSections(userId: Uuid, scope: String, language: String): List<DynamicSection>? = null
        override suspend fun saveSections(
            userId: Uuid,
            scope: String,
            language: String,
            sections: List<DynamicSection>
        ) {
        }

        override suspend fun invalidateUser(userId: Uuid) {}
        override suspend fun invalidateAll() {}
    },
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

interface UserPluginSettings {
    suspend fun getString(userId: Uuid, key: String): String?
    suspend fun setString(userId: Uuid, key: String, value: String)
    suspend fun getBoolean(userId: Uuid, key: String, defaultValue: Boolean = false): Boolean
    suspend fun setBoolean(userId: Uuid, key: String, value: Boolean)
    fun observeString(userId: Uuid, key: String, defaultValue: String? = null): Flow<String?>
    fun observeBoolean(userId: Uuid, key: String, defaultValue: Boolean = false): Flow<Boolean>
}

class SidebarRegistry {
    private var provider: ((Uuid?) -> Flow<List<SidebarItem>>)? = null

    fun onSidebar(handler: (Uuid?) -> Flow<List<SidebarItem>>) {
        provider = handler
    }

    fun getFlow(userId: Uuid?): Flow<List<SidebarItem>>? = provider?.invoke(userId)
}

interface AffinityVectorStore {
    val vectorUpdates: Flow<Uuid>
    suspend fun getVector(userId: Uuid): AffinityVector?
    suspend fun saveVector(userId: Uuid, vector: AffinityVector, eventCount: Int)
    suspend fun getPendingUsers(limit: Int): List<Uuid>
    suspend fun getUserEventCount(userId: Uuid): Int
    suspend fun getCachedEventCount(userId: Uuid): Int?
}

interface GenreDictionaryProvider {
    suspend fun getLocalizedGenres(language: String = "ru"): Map<String, String>
}
