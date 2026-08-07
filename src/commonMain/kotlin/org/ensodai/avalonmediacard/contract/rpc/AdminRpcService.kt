package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.admin.AdminActionResponse
import org.ensodai.avalonmediacard.contract.admin.CreateUserRequest

@Rpc
interface AdminRpcService {
    suspend fun uploadPlugin(fileName: String, fileContent: ByteArray): Boolean
    suspend fun createUser(request: CreateUserRequest): AdminActionResponse
}
