package org.ensodai.avalonmediacard.contract.admin

import kotlinx.serialization.Serializable

@Serializable
enum class ProxyType {
    HTTP,
    SOCKS5
}

@Serializable
data class PluginProxySettingDto(
    val pluginId: String,
    val pluginName: String,
    val pluginVersion: String,
    val isEnabled: Boolean = false,
    val type: ProxyType = ProxyType.HTTP,
    val host: String = "",
    val port: Int = 8080,
    val username: String? = null,
    val password: String? = null
)

@Serializable
data class UpdatePluginProxyRequest(
    val pluginId: String,
    val isEnabled: Boolean,
    val type: ProxyType = ProxyType.HTTP,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)

@Serializable
data class TestPluginProxyRequest(
    val type: ProxyType = ProxyType.HTTP,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
