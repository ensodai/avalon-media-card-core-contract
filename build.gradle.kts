import java.time.LocalDate

plugins {
    kotlin("multiplatform") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
    id("com.android.kotlin.multiplatform.library") version "9.1.1"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.3"
    `maven-publish`
}

group = "org.ensodai.avalonmediacard"
version = providers.gradleProperty("core.version").getOrElse("1.0.1")

val generateCoreVersion = tasks.register("generateCoreVersion") {
    val outputDir = layout.buildDirectory.dir("generated/source/coreVersion/commonMain/kotlin")
    val coreVersion = providers.gradleProperty("core.version").getOrElse("1.0.1")
    val apiLevel = providers.gradleProperty("core.apiLevel").getOrElse("4")
    val protocolVersion = providers.gradleProperty("core.protocolVersion").getOrElse("2.0")
    val buildDate = LocalDate.now().toString()

    inputs.property("version", coreVersion)
    inputs.property("apiLevel", apiLevel)
    inputs.property("protocolVersion", protocolVersion)
    inputs.property("buildDate", buildDate)
    outputs.dir(outputDir)

    doLast {
        val file = outputDir.get().file("org/ensodai/avalonmediacard/contract/version/CoreVersion.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package org.ensodai.avalonmediacard.contract.version

            object CoreVersion {
                const val VERSION: String = "$coreVersion"
                const val API_LEVEL: Int = $apiLevel
                const val PROTOCOL_VERSION: String = "$protocolVersion"
                const val BUILD_DATE: String = "$buildDate"

                fun getDisplayVersion(customVersion: String? = null): String {
                    val ver = customVersion?.takeIf { it.isNotBlank() } ?: VERSION
                    return "Avalon Media Card v${'$'}ver (API v${'$'}API_LEVEL, Protocol v${'$'}PROTOCOL_VERSION)"
                }
            }
            """.trimIndent()
        )
    }
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "org.ensodai.avalonmediacard.contract"
        compileSdk = 37
        minSdk = 26
    }

    jvm()
    
    iosArm64()
    iosSimulatorArm64()

    js {
        browser()
    }
    
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    sourceSets {
        all {
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
        val commonMain by getting {
            kotlin.srcDir(generateCoreVersion.map { it.outputs.files.singleFile })
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                api("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.10.2")
                api("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.10.2")
                api("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-cbor:0.10.2")
                implementation("io.ktor:ktor-client-core:3.5.0")
                api("co.touchlab:kermit:2.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

