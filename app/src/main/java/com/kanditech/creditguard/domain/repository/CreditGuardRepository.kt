package com.kanditech.creditguard.domain.repository

import com.kanditech.creditguard.data.local.entities.*
import com.kanditech.creditguard.data.remote.dto.*
import kotlinx.coroutines.flow.Flow

interface CreditGuardRepository {

    // Auth
    suspend fun login(username: String, password: String): Result<LoginResponse>

    // Dashboard
    fun getDashboardData(): Flow<CreditorEntity?>
    suspend fun refreshDashboard(): Result<Unit>

    // Groups
    fun getGroups(): Flow<List<GroupEntity>>
    suspend fun refreshGroups(): Result<Unit>
    suspend fun getGroupDetails(id: String): Flow<GroupEntity?>

    // Collection
    suspend fun collectPayment(groupId: String, amount: Double, notes: String?): Result<Unit>
    suspend fun syncPendingCollections(): Result<Unit>

    // Wallet
    fun getWalletTransactions(): Flow<List<WalletTransactionEntity>>
    suspend fun refreshWallet(): Result<Unit>
    suspend fun addMoneyToWallet(amount: Double, reason: String): Result<Unit>

    // Customers
    fun getCustomers(): Flow<List<CustomerEntity>>
    suspend fun refreshCustomers(): Result<Unit>
    suspend fun createCustomer(request: CreateCustomerRequest): Result<CustomerDto>

    // Credit
    suspend fun createGroupWithCredit(request: CreateGroupRequest): Result<GroupDto>
}
