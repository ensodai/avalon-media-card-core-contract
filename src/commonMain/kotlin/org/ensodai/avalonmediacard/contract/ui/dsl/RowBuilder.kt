package org.ensodai.avalonmediacard.contract.ui.dsl

import org.ensodai.avalonmediacard.contract.*
import kotlin.uuid.Uuid

class RowBuilder(private val id: Uuid?) : UiContainerBuilder {
    private val children = mutableListOf<UiComponent>()
    private val modifiers = mutableListOf<UiModifier>()

    override fun add(component: UiComponent) {
        children.add(component)
    }

    fun padding(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
        modifiers.add(UiModifierPadding(start, top, end, bottom))
    }

    fun size(width: Int? = null, height: Int? = null) {
        modifiers.add(UiModifierSize(width, height))
    }

    fun clickable(action: UiAction) {
        modifiers.add(UiModifierClickable(action))
    }

    fun clip(cornerRadiusDp: Int) {
        modifiers.add(UiModifierClipRounded(cornerRadiusDp))
    }

    fun background(colorHex: String) {
        modifiers.add(UiModifierBackground(colorHex))
    }

    fun build() = UiRow(id = id, children = children, modifiers = modifiers)
}
