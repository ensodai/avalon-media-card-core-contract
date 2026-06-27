package org.ensodai.avalonmediacard.contract

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.slot.Action

@Rpc
interface ActionRpcService {
    suspend fun handleAction(action: Action)
}
