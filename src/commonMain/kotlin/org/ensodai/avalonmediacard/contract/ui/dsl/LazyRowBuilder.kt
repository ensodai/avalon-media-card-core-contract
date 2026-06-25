package org.ensodai.avalonmediacard.contract.ui.dsl

import org.ensodai.avalonmediacard.contract.*
import kotlin.uuid.Uuid

class LazyRowBuilder(private val id: Uuid?) : UiContainerBuilder {
    private val children = mutableListOf<UiComponent>()
    private val modifiers = mutableListOf<UiModifier>()

    override fun add(component: UiComponent) {
        children.add(component)
    }

    fun build() = UiLazyRow(id = id, children = children, modifiers = modifiers)
}
