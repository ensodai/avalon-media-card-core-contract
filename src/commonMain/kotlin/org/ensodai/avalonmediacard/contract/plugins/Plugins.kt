package org.ensodai.avalonmediacard.contract.plugins

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.ensodai.avalonmediacard.contract.Screen
import org.ensodai.avalonmediacard.contract.model.MediaCatalog
import org.ensodai.avalonmediacard.contract.slot.Action
import org.ensodai.avalonmediacard.contract.slot.ActionCommand
import org.ensodai.avalonmediacard.contract.slot.SlotUpdate
import kotlin.reflect.KClass

/**
 * Базовый интерфейс для любого плагина в системе Avalon.
 * 
 * Жизненный цикл:
 * - Инициализируется один раз при старте сервера через [onInitialize].
 * - Уничтожается при остановке сервера через [onDestroy].
 */
interface AvalonPlugin {
    val id: String
    val name: String
    val version: String
    val author: String

    fun onInitialize(context: PluginContext) {}
    fun onDestroy() {}
}

/**
 * Реестр слотов плагина.
 * Плагин использует его для декларативного описания того, на каких экранах он работает.
 */
class SlotRegistry {
    @PublishedApi
    internal val handlers = mutableMapOf<KClass<out Screen>, (Screen, kotlin.uuid.Uuid?) -> List<Flow<SlotUpdate>>>()

    inline fun <reified T : Screen> onScreen(noinline handler: (T) -> List<Flow<SlotUpdate>>) {
        handlers[T::class] = { screen, _ -> handler(screen as T) }
    }

    inline fun <reified T : Screen> onScreenWithUser(noinline handler: (T, kotlin.uuid.Uuid?) -> List<Flow<SlotUpdate>>) {
        handlers[T::class] = { screen, userId -> handler(screen as T, userId) }
    }

    fun getFlowsForScreen(screen: Screen, userId: kotlin.uuid.Uuid? = null): List<Flow<SlotUpdate>> {
        return handlers[screen::class]?.invoke(screen, userId) ?: emptyList()
    }
}

/**
 * Реестр команд плагина.
 * Плагин использует его для регистрации обработчиков интерактивных действий от клиента.
 */
class CommandRegistry {
    @PublishedApi
    internal val serializersByClass = mutableMapOf<KClass<*>, KSerializer<*>>()

    @PublishedApi
    internal val handlers = mutableMapOf<KClass<*>, MutableList<suspend (Any) -> Unit>>()

    inline fun <reified Cmd : Any> bind(noinline handler: suspend (Cmd) -> Unit) {
        val klass = Cmd::class
        if (klass !in serializersByClass) {
            serializersByClass[klass] = serializer<Cmd>()
        }
        @Suppress("UNCHECKED_CAST")
        handlers.getOrPut(klass) { mutableListOf() }.add(handler as suspend (Any) -> Unit)
    }

    val serializers: List<KSerializer<*>>
        get() = serializersByClass.values.toList()

    suspend fun dispatch(command: Any) {
        handlers[command::class]?.forEach { it(command) }
    }
    
    fun canHandle(commandName: String): Boolean {
        return serializersByClass.values.any { it.descriptor.serialName == commandName }
    }
}

/**
 * Реестр потоков (видео/аудио) плагина.
 * Плагин использует его для выдачи потоков (видео плеера) для фильма/сериала.
 */
class StreamRegistry {
    private var provider: ((String, Int?, Int?) -> Flow<MediaStream>)? = null
    
    fun onMedia(handler: (String, Int?, Int?) -> Flow<MediaStream>) {
        provider = handler
    }
    
    fun getStreams(mediaId: String, season: Int?, episode: Int?): Flow<MediaStream>? = provider?.invoke(mediaId, season, episode)
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
    val actions: Flow<Action>
    suspend fun post(action: Action)
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
    val dispatcher: PluginCallDispatcher,
    val actionBus: ActionBus,
    val catalog: MediaCatalog,
    val userMovies: UserMovieProvider,
    val userCustomLists: UserCustomListProvider,
    val settings: PluginSettings,
    val slots: SlotRegistry = SlotRegistry(),
    val commands: CommandRegistry = CommandRegistry(),
    val streams: StreamRegistry = StreamRegistry(),
    val sidebars: SidebarRegistry = SidebarRegistry(),
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    suspend fun call(targetPluginId: String, command: String, payloadJson: String): String {
        return dispatcher.call(targetPluginId, command, payloadJson)
    }
}

interface PluginSettings {
    suspend fun getString(key: String): String?
    suspend fun setString(key: String, value: String)
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    suspend fun setBoolean(key: String, value: Boolean)
    fun observeString(key: String, defaultValue: String? = null): Flow<String?>
    fun observeBoolean(key: String, defaultValue: Boolean = false): Flow<Boolean>
}

class SidebarRegistry {
    private var provider: ((kotlin.uuid.Uuid?) -> Flow<List<org.ensodai.avalonmediacard.contract.SidebarItem>>)? = null

    fun onSidebar(handler: (kotlin.uuid.Uuid?) -> Flow<List<org.ensodai.avalonmediacard.contract.SidebarItem>>) {
        provider = handler
    }

    fun getFlow(userId: kotlin.uuid.Uuid?): Flow<List<org.ensodai.avalonmediacard.contract.SidebarItem>>? = provider?.invoke(userId)
}

fun <T : Any> T.toAction(pluginId: String, serializer: KSerializer<T>): ActionCommand {
    val json = Json { ignoreUnknownKeys = true }
    return ActionCommand(
        pluginId = pluginId,
        commandId = serializer.descriptor.serialName,
        payload = json.encodeToString(serializer, this)
    )
}

inline fun <reified T : Any> T.toAction(pluginId: String = ""): ActionCommand =
    this.toAction(pluginId, serializer<T>())
