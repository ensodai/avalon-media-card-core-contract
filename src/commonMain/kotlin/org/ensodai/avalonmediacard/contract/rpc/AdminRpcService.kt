package org.ensodai.avalonmediacard.contract

import kotlinx.rpc.annotations.Rpc

@Rpc
interface AdminRpcService {
    suspend fun uploadPlugin(fileName: String, fileContent: ByteArray): Boolean
}
