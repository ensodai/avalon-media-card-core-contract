package org.ensodai.avalonmediacard.contract.plugins

import org.ensodai.avalonmediacard.contract.model.AffinityVector
import kotlin.uuid.Uuid

interface RecommendationEngine {
    suspend fun getAffinityVector(userId: Uuid): AffinityVector
}
