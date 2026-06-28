package org.ensodai.avalonmediacard.contract.plugins

import org.ensodai.avalonmediacard.contract.MediaKey
import kotlin.uuid.Uuid

data class CustomListStatus(
    val id: String,
    val name: String,
    val isAdded: Boolean
)

data class CustomListInfo(
    val id: Uuid,
    val name: String
)

interface UserCustomListProvider {
    fun observeUserLists(userId: Uuid): kotlinx.coroutines.flow.Flow<List<CustomListInfo>>
    fun observeListItems(listId: Uuid): kotlinx.coroutines.flow.Flow<List<MediaKey>>
    suspend fun getCustomListsWithStatus(userId: Uuid, mediaKey: MediaKey): List<CustomListStatus>
    suspend fun toggleList(userId: Uuid, listId: String, mediaKey: MediaKey)
    suspend fun createList(userId: Uuid, listName: String, mediaKey: MediaKey)
}
