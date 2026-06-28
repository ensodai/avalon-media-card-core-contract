package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.ensodai.avalonmediacard.contract.Screen
import kotlin.uuid.Uuid

@Serializable
sealed interface Action

@Serializable 
data class ActionNavigate(val screen: Screen) : Action

@Serializable 
data class ActionPlayVideo(val url: String, val title: String) : Action

@Serializable 
data class ActionOpenUrl(val url: String) : Action

interface UserAwareCommand {
    var userId: Uuid?
}

@Serializable 
data class ActionCommand(
    val pluginId: String,
    val commandId: String,
    val payload: String = "{}"
) : Action
fun Action.withParameter(key: String, value: Number): Action {
    if (this !is ActionCommand) return this
    val originalObj = Json.parseToJsonElement(this.payload).jsonObject
    val map = originalObj.toMutableMap()
    map[key] = JsonPrimitive(value)
    return this.copy(payload = JsonObject(map).toString())
}

fun Action.withParameter(key: String, value: String): Action {
    if (this !is ActionCommand) return this
    val originalObj = Json.parseToJsonElement(this.payload).jsonObject
    val map = originalObj.toMutableMap()
    map[key] = JsonPrimitive(value)
    return this.copy(payload = JsonObject(map).toString())
}

fun Action.withParameter(key: String, value: Boolean): Action {
    if (this !is ActionCommand) return this
    val originalObj = Json.parseToJsonElement(this.payload).jsonObject
    val map = originalObj.toMutableMap()
    map[key] = JsonPrimitive(value)
    return this.copy(payload = JsonObject(map).toString())
}
