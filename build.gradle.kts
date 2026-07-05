plugins {
    kotlin("multiplatform") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.2"
    `maven-publish`
}

group = "org.ensodai.avalonmediacard"
version = "1.0.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)

    jvm()
    
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
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                api("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.10.2")
                api("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.10.2")
                api("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-cbor:0.10.2")
                implementation("io.ktor:ktor-client-core:3.5.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
