package org.ensodai.avalonmediacard.contract

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.slot.ActionResult
import org.ensodai.avalonmediacard.contract.slot.ServerAction

@Rpc
interface ActionRpcService {
    suspend fun handleAction(action: ServerAction): ActionResult
}
