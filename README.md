# 🌟 Avalon Media Card Core Contract

> **Единый контракт и API-протокол экосистемы Avalon Media Card.**

`avalon-media-card-core-contract` — это общий Kotlin Multiplatform (KMP) модуль, выступающий в роли **Single Source of Truth** (единого источника истины) для всей платформы:
- 🖥️ **Серверного ядра (Ktor Backend)**
- 📱 **Кроссплатформенного клиента (Compose Multiplatform: Desktop, Android, Web Wasm)**
- 🧩 **Плагинов расширения (Plugin Ecosystem)**

---

## 🎯 Для чего нужен Core Contract?

1. **Server-Driven UI (SDUI)**: Описание разметки экранов, слотов (`HeroBanner`, `Carousel`, `Banner`, `Exploration`, `Details` и др.), состояний (`Loading`, `Content`, `Error`, `Empty`) и действий пользователя (`Action`).
2. **Сетевые контракты (Kotlin-RPC)**: Строго типизированные интерфейсы удаленного вызова процедур (`@Rpc`) для стриминга экранов, авторизации, воспроизведения и телеметрии.
3. **Plugin SDK**: Интерфейсы и контексты (`AvalonPlugin`, `PluginContext`, `PluginSettings`, `PluginIntegrationManager`) для создания изолированных плагинов контента, метаданных и интеграций.
4. **Кроссплатформенная сериализация**: Поддержка `kotlinx.serialization` (JSON и CBOR) для обмена данными между JVM, WebAssembly и Android.

---

## 🚀 Подключение к плагину для разработки

### 1. Настройка репозиториев (`settings.gradle.kts`)

Добавьте репозитории в блок `dependencyResolutionManagement`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://jitpack.io") }
        
        // (Опционально) Если вы используете GitHub Packages:
        // maven {
        //     url = uri("https://maven.pkg.github.com/ensodai/avalon-media-card-core-contract")
        //     credentials {
        //         username = System.getenv("GITHUB_ACTOR") ?: "ensodai"
        //         password = System.getenv("GITHUB_TOKEN")
        //     }
        // }
    }
}
```

> 💡 **Совет для монорепозитория**: Если контракт находится рядом на диске, в `settings.gradle.kts` можно просто добавить композитную сборку:
> ```kotlin
> if (file("../avalon-media-card-core-contract").exists()) {
>     includeBuild("../avalon-media-card-core-contract")
> }
> ```

---

### 2. Подключение зависимости (`build.gradle.kts`)

#### Вариант A: Через `gradle/libs.versions.toml` (Рекомендуется)

В `gradle/libs.versions.toml`:
```toml
[versions]
avalon-core-contract = "1.0.0"

[libraries]
avalon-core-contract = { module = "org.ensodai.avalonmediacard:avalon-media-card-core-contract", version.ref = "avalon-core-contract" }
```

В `build.gradle.kts` плагина:
```kotlin
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.avalon.core.contract)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}
```

#### Вариант B: Прямая зависимость

```kotlin
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.0"
}

dependencies {
    implementation("org.ensodai.avalonmediacard:avalon-media-card-core-contract:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }
}
```

---

## 🛠️ Пример создания плагина

### 1. Реализация интерфейса `AvalonPlugin`

Создайте класс плагина, реализующий `AvalonPlugin`:

```kotlin
package com.example.myplugin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ensodai.avalonmediacard.contract.model.SidebarItem
import org.ensodai.avalonmediacard.contract.plugins.AvalonPlugin
import org.ensodai.avalonmediacard.contract.plugins.PluginContext
import org.ensodai.avalonmediacard.contract.plugins.PluginManifest
import org.ensodai.avalonmediacard.contract.slot.*
import org.ensodai.avalonmediacard.contract.ui.navigation.Screen

class MyCustomPlugin : AvalonPlugin {

    override val manifest = PluginManifest(
        id = "my-custom-plugin",
        name = "My Custom Plugin",
        version = "1.0.0",
        author = "Developer",
        description = "Пример кастомного плагина для Avalon Media Card"
    )

    override fun init(context: PluginContext) {
        // 1. Декларация слотов и разметки для Главного экрана
        context.slots.declare<Screen.Dashboard>(
            slots = listOf(SlotId.Carousels),
            manifestLayout = { userId ->
                listOf(LayoutNode(nodeId = "my_custom_carousel", slotId = SlotId.Carousels))
            }
        )

        // 2. Поставка данных в слот экрана
        context.screens.onScreen<Screen.Dashboard> { screen, userId ->
            listOf(
                SlotUpdate(
                    slotId = SlotId.Carousels,
                    nodeId = "my_custom_carousel",
                    state = SlotState.Content(
                        SlotData.Carousel(
                            title = "Популярное от плагина",
                            items = listOf(
                                MediaCardItem(
                                    id = "item_1",
                                    title = "Тестовый фильм",
                                    posterUrl = "https://example.com/poster.jpg",
                                    action = ActionNavigate(Screen.MovieDetails(id = "item_1"))
                                )
                            )
                        )
                    )
                )
            )
        }

        // 3. Добавление пункта в боковое меню (Sidebar)
        context.sidebar.provide { userId ->
            flow {
                emit(
                    listOf(
                        SidebarItem(
                            itemId = "my_custom_screen",
                            title = "Мой раздел",
                            route = "my_custom_screen",
                            order = 10
                        )
                    )
                )
            }
        }
    }
}
```

---

### 2. Регистрация через Java SPI (ServiceLoader)

Чтобы сервер Avalon автоматически обнаружил ваш JAR-плагин, создайте файл:

`src/main/resources/META-INF/services/org.ensodai.avalonmediacard.contract.plugins.AvalonPlugin`

С полным именем вашего класса:
```text
com.example.myplugin.MyCustomPlugin
```

---

### 3. Сборка JAR-плагина

В `build.gradle.kts` настройте имя собираемого JAR-архива и автоматическое копирование в серверную папку плагинов:

```kotlin
tasks.named<Jar>("jar") {
    archiveFileName.set("my-custom-plugin.jar")
}

val copyPluginJar = tasks.register<Copy>("copyPluginJar") {
    from(tasks.named("jar"))
    into(rootProject.file("server/plugins"))
}

tasks.named("build") {
    dependsOn(copyPluginJar)
}
```

Соберите плагин командой:
```bash
./gradlew :my-custom-plugin:build
```

Поместите полученный `.jar` в директорию `plugins/` сервера Avalon Media Card и перезапустите сервер — плагин загрузится автоматически! 🚀

---

## 📦 Основные компоненты контракта

| Пакет | Описание |
|---|---|
| `org.ensodai.avalonmediacard.contract.plugins` | Интерфейсы `AvalonPlugin`, `PluginContext`, `PluginSettings`, `PluginIntegrationManager` |
| `org.ensodai.avalonmediacard.contract.slot` | Модели Server-Driven UI: `SlotId`, `SlotData`, `SlotState`, `SlotUpdate`, `LayoutNode`, `Action` |
| `org.ensodai.avalonmediacard.contract.rpc` | Интерфейсы Kotlin-RPC (`SduiRpcService`, `PlaybackRpcService`, `AdminRpcService` и др.) |
| `org.ensodai.avalonmediacard.contract.model` | Доменные DTO: `MediaSource`, `PlaybackStream`, `SidebarItem`, `UserSettingsDto` |
| `org.ensodai.avalonmediacard.contract.i18n` | Интерфейс локализации плагинов `PluginI18n` |
| `org.ensodai.avalonmediacard.contract.ui.navigation` | Навигационные экраны `Screen.*` (`Dashboard`, `Movies`, `TvShows`, `MovieDetails`, `Admin`, `Settings`...) |