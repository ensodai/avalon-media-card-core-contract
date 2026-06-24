package org.ensodai.avalonmediacard.contract.plugins

import org.ensodai.avalonmediacard.contract.UiAction
import org.ensodai.avalonmediacard.contract.UiActionResponse

/**
 * Возможность плагина обрабатывать действия (клики, пагинацию, кастомные экшены).
 */
fun interface ActionHandler {
    suspend fun handleAction(action: UiAction): UiActionResponse
}
