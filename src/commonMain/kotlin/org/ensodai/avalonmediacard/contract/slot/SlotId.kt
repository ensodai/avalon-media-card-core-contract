package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable

@Serializable
enum class SlotId {
    Header,
    PlayButtons,
    CollectionButtons,
    ContinueWatching,
    UserActions,
    SyncStatus,
    Description,
    TvSeasons,
    Cast,
    Carousels,
    Comments,
    Sidebar,
    HomeWidget,
    Integrations,
    PersonHeader,
    PersonImages,
    PersonBio,
    PersonCredits,
    MediaList,
    CollectionGrid,
    CustomListGrid
}
