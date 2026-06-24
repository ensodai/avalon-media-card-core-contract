package org.ensodai.avalonmediacard.contract.ui.dsl

import org.ensodai.avalonmediacard.contract.UiAction
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import org.ensodai.avalonmediacard.contract.UiModifierBackground
import org.ensodai.avalonmediacard.contract.UiModifierClickable
import org.ensodai.avalonmediacard.contract.UiModifierClipRounded
import org.ensodai.avalonmediacard.contract.UiModifierPadding
import org.ensodai.avalonmediacard.contract.UiModifierSize
import org.ensodai.avalonmediacard.contract.ui.components.UiBox
import kotlin.uuid.Uuid

class BoxBuilder(private val id: Uuid?) : UiContainerBuilder {
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

    fun build() = UiBox(id = id, children = children, modifiers = modifiers)
}
