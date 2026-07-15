package org.ensodai.avalonmediacard.contract

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.model.TelemetryEvent

@Rpc
interface TelemetryRpcService {
    suspend fun logEvent(event: TelemetryEvent)
}
