package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.coroutines.flow.Flow
import org.ensodai.avalonmediacard.contract.UiComponent

/**
 * Возможность плагина интегрировать UI-виджеты в интерфейс ядра.
 */
interface WidgetProvider {
    fun renderWidget(target: WidgetTarget): Flow<List<UiComponent>>
}
