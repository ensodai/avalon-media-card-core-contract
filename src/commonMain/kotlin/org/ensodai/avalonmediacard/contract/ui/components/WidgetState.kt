package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiAction

@Serializable
enum class WidgetType {
    Carousel, Grid, Cast, DetailsHeader, PersonHeader
}

@Serializable
sealed class WidgetState {
    @Serializable
    data object Loading : WidgetState()
    @Serializable
    data class Success(val component: UiComponent) : WidgetState()
    @Serializable
    data class Error(val message: String, val retryAction: UiAction? = null) : WidgetState()
}
