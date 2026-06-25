package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.ensodai.avalonmediacard.contract.UiComponent
import kotlin.reflect.KClass

/**
 * Модуль плагина — изолированная фича со своим UI и командами.
 *
 * Модуль сам решает, на какой [WidgetTarget] реагировать в [render].
 * Бэкенд-модуль без UI просто не переопределяет [render].
 */
interface PluginModule {
    val moduleId: String

    /**
     * Возвращает поток UI-компонентов для данного таргета.
     * По умолчанию — пустой список (модуль без UI).
     */
    fun render(target: WidgetTarget): Flow<List<UiComponent>> = flowOf(emptyList())

    /**
     * Регистрация команд модуля через типизированный [CommandBinder].
     */
    fun registerCommands(binder: CommandBinder) {}

    fun onDestroy() {}
}

/**
 * Типизированная регистрация команд без `.serializer()`.
 *
 * Использование:
 * ```
 * binder.bind<LoadMoreFeed> { cmd ->
 *     loadPage(cmd.page)
 * }
 * ```
 *
 * Поддерживает несколько обработчиков на один тип команды —
 * все зарегистрированные обработчики будут вызваны при dispatch.
 */
class CommandBinder {
    @PublishedApi
    internal val serializersByClass = mutableMapOf<KClass<*>, KSerializer<*>>()

    @PublishedApi
    internal val handlers = mutableMapOf<KClass<*>, MutableList<suspend (Any) -> Unit>>()

    /**
     * Регистрирует типизированный обработчик команды.
     * Сериализатор выводится автоматически через reified type parameter.
     */
    inline fun <reified Cmd : Any> bind(noinline handler: suspend (Cmd) -> Unit) {
        val klass = Cmd::class
        if (klass !in serializersByClass) {
            serializersByClass[klass] = serializer<Cmd>()
        }
        @Suppress("UNCHECKED_CAST")
        handlers.getOrPut(klass) { mutableListOf() }.add(handler as suspend (Any) -> Unit)
    }

    /** Список сериализаторов всех зарегистрированных команд. */
    val serializers: List<KSerializer<*>>
        get() = serializersByClass.values.toList()

    /** Вызывает все обработчики, зарегистрированные для типа данной команды. */
    internal suspend fun dispatch(command: Any) {
        handlers[command::class]?.forEach { it(command) }
    }
}

/**
 * Базовый класс модульного плагина.
 *
 * Автоматически собирает [widgetProvider], [commandSerializers] и [onCommand]
 * из зарегистрированных [PluginModule].
 *
 * Использование:
 * ```
 * class MyPlugin : ModularPlugin() {
 *     override val id = "org.ensodai.myplugin"
 *     override val name = "My Plugin"
 *     override val version = "1.0.0"
 *     override val author = "Dev"
 *
 *     override fun setupModules(context: PluginContext) {
 *         addModule(FeatureA(context))
 *         addModule(FeatureB(context))
 *     }
 * }
 * ```
 */
abstract class ModularPlugin : AvalonPlugin {
    private val modules = mutableListOf<PluginModule>()
    private val binder = CommandBinder()

    /** Скоуп плагина — создаётся в [onInitialize], отменяется в [onDestroy]. */
    protected lateinit var pluginScope: CoroutineScope
        private set

    /** Контекст плагина — доступен после [onInitialize]. */
    protected lateinit var pluginContext: PluginContext
        private set

    /**
     * Регистрация модулей плагина.
     * Вызывается из [onInitialize] после инициализации скоупа и контекста.
     */
    abstract fun setupModules(context: PluginContext)

    /** Добавляет модуль и автоматически регистрирует его команды. */
    protected fun addModule(module: PluginModule) {
        modules += module
        module.registerCommands(binder)
    }

    // --- Автоматическая сборка из модулей ---

    override val commandSerializers: List<KSerializer<*>>
        get() = binder.serializers

    private val _widgetProvider = object : WidgetProvider {
        override fun renderWidget(target: WidgetTarget): Flow<List<UiComponent>> {
            val flows = modules.map { it.render(target) }
            if (flows.isEmpty()) return flowOf(emptyList())
            return combine(flows) { arrays -> arrays.toList().flatten() }
        }
    }

    override val widgetProvider: WidgetProvider
        get() = _widgetProvider

    override fun onInitialize(context: PluginContext) {
        pluginScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        pluginContext = context
        setupModules(context)
    }

    override suspend fun onCommand(command: Any) {
        binder.dispatch(command)
    }

    override fun onDestroy() {
        modules.forEach { it.onDestroy() }
        pluginScope.cancel()
    }
}
