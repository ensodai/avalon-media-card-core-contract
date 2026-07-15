package org.ensodai.avalonmediacard.contract.plugins

interface RecommendationEngineRegistrar {
    fun registerEngine(engine: RecommendationEngine)
    fun unregisterEngine()
}
