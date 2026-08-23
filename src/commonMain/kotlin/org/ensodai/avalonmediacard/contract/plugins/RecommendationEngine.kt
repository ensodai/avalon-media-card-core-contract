package org.ensodai.avalonmediacard.contract.plugins

import org.ensodai.avalonmediacard.contract.model.AffinityVector
import org.ensodai.avalonmediacard.contract.model.DynamicSection
import kotlin.uuid.Uuid

interface RecommendationEngine {
    suspend fun getAffinityVector(userId: Uuid): AffinityVector
    suspend fun generateDashboard(userId: Uuid, language: String = "ru"): List<DynamicSection>
    suspend fun generateTab(userId: Uuid, scope: String, language: String = "ru"): List<DynamicSection>
}
