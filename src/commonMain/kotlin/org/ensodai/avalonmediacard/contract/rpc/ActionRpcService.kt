package org.ensodai.avalonmediacard.contract

import kotlinx.rpc.annotations.Rpc

@Rpc
interface ActionRpcService {
    suspend fun handleAction(action: UiAction): UiActionResponse
}
