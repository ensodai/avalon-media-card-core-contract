package org.ensodai.avalonmediacard.contract.ui.dsl

import org.ensodai.avalonmediacard.contract.UiAction
import org.ensodai.avalonmediacard.contract.UiModifier
import org.ensodai.avalonmediacard.contract.UiModifierClickable
import org.ensodai.avalonmediacard.contract.UiModifierClipRounded
import org.ensodai.avalonmediacard.contract.UiModifierPadding
import org.ensodai.avalonmediacard.contract.UiModifierSize

class ModifierBuilder {
    private val modifiers = mutableListOf<UiModifier>()

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

    fun build() = modifiers
}
