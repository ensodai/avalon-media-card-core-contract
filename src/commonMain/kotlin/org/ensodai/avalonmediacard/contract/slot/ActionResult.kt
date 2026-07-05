package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.Screen

@Serializable
sealed interface ActionResult {
    
    // Успешное выполнение, требующее точечного обновления определенных слотов на экране.
    @Serializable
    data class SuccessSlotUpdate(val slots: List<SlotData>) : ActionResult
    
    // Команда клиенту осуществить переход на другой экран
    @Serializable
    data class Navigate(val screen: Screen) : ActionResult
    
    // Отобразить всплывающее уведомление
    @Serializable
    data class ShowNotification(val message: String, val type: String = "info") : ActionResult
    
    // Бизнес-ошибка, которую клиент должен обработать
    @Serializable
    data class Error(val code: Int, val message: String) : ActionResult
    
    // Операция выполнена успешно, но изменений в UI не требуется
    @Serializable
    data object NoOp : ActionResult
    
    // Команда клиенту выполнить действие локально (например, открыть плеер)
    @Serializable
    data class ExecuteAction(val action: Action) : ActionResult
}
