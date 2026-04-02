package com.kanditech.creditguard.data.repository

import com.kanditech.creditguard.data.local.dao.CreditGuardDao
import com.kanditech.creditguard.data.local.entities.*
import com.kanditech.creditguard.data.remote.CreditGuardApi
import com.kanditech.creditguard.data.remote.dto.*
import com.kanditech.creditguard.domain.repository.CreditGuardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class CreditGuardRepositoryImpl @Inject constructor(
    private val api: CreditGuardApi,
    private val dao: CreditGuardDao
) : CreditGuardRepository {

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        return handleApi { api.login(LoginRequest(username, password)) }
    }

    override fun getDashboardData(): Flow<CreditorEntity?> {
        return dao.getCreditor()
    }

    override suspend fun refreshDashboard(): Result<Unit> {
        val result = handleApi { api.getDashboard() }
        return if (result.isSuccess) {
            val dto = result.getOrNull()!!
            dao.insertCreditor(
                CreditorEntity(
                    id = "current", // Using single creditor for now
                    firstName = "",
                    lastName = "",
                    phone = "",
                    walletBalance = dto.walletBalance,
                    todayCollection = dto.todayCollection,
                    expectedCollectionToday = dto.expectedCollectionToday,
                    activeGroupsCount = dto.activeGroupsCount
                )
            )
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override fun getGroups(): Flow<List<GroupEntity>> {
        return dao.getAllGroups()
    }

    override suspend fun refreshGroups(): Result<Unit> {
        val result = handleApi { api.getGroups() }
        return if (result.isSuccess) {
            val dtos = result.getOrNull()!!
            dao.insertGroups(dtos.map { it.toEntity() })
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override suspend fun getGroupDetails(id: String): Flow<GroupEntity?> = flow {
        // Emit from local first
        val local = dao.getGroupById(id)
        emit(local)
        // Refresh from remote
        val result = handleApi { api.getGroupDetails(id) }
        if (result.isSuccess) {
            val dto = result.getOrNull()!!
            dao.insertGroups(listOf(dto.toEntity()))
            emit(dto.toEntity())
        }
    }

    override suspend fun collectPayment(groupId: String, amount: Double, notes: String?): Result<Unit> {
        val request = CollectionRequest(groupId, amount, System.currentTimeMillis(), notes)
        val result = handleApi { api.collectPayment(request) }
        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            // Store as pending for offline
            dao.insertPendingCollection(
                PendingCollectionEntity(
                    groupId = groupId,
                    amount = amount,
                    date = request.date,
                    collectorId = "current",
                    notes = notes
                )
            )
            Result.success(Unit) // Mark as success because it's queued
        }
    }

    override suspend fun syncPendingCollections(): Result<Unit> {
        val pending = dao.getPendingCollections()
        if (pending.isEmpty()) return Result.success(Unit)

        val requests = pending.map { CollectionRequest(it.groupId, it.amount, it.date, it.notes) }
        val result = handleApi { api.syncCollections(requests) }
        return if (result.isSuccess) {
            pending.forEach { dao.deletePendingCollection(it.id) }
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override fun getWalletTransactions(): Flow<List<WalletTransactionEntity>> {
        return dao.getWalletTransactions()
    }

    override suspend fun refreshWallet(): Result<Unit> {
        val result = handleApi { api.getWalletTransactions() }
        return if (result.isSuccess) {
            val dtos = result.getOrNull()!!
            dao.insertWalletTransactions(dtos.map { it.toEntity() })
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override suspend fun addMoneyToWallet(amount: Double, reason: String): Result<Unit> {
        return handleApi { api.addMoneyToWallet(AddMoneyRequest(amount, reason)) }
    }

    override fun getCustomers(): Flow<List<CustomerEntity>> {
        return dao.getAllCustomers()
    }

    override suspend fun refreshCustomers(): Result<Unit> {
        val result = handleApi { api.getCustomers() }
        return if (result.isSuccess) {
            val dtos = result.getOrNull()!!
            dao.insertCustomers(dtos.map { it.toEntity() })
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override suspend fun createCustomer(request: CreateCustomerRequest): Result<CustomerDto> {
        return handleApi { api.createCustomer(request) }
    }

    override suspend fun createGroupWithCredit(request: CreateGroupRequest): Result<GroupDto> {
        return handleApi { api.createGroup(request) }
    }

    private suspend fun <T> handleApi(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun GroupDto.toEntity() = GroupEntity(
        id = id,
        name = name,
        leaderId = leaderId,
        leaderName = leaderName,
        location = location,
        photoUrl = photoUrl,
        isActive = isActive,
        outstandingAmount = outstandingAmount,
        dailyInstallment = dailyInstallment,
        totalRepaidPercent = totalRepaidPercent
    )

    private fun WalletTransactionDto.toEntity() = WalletTransactionEntity(
        id = id,
        type = type,
        amount = amount,
        date = date,
        reason = reason
    )

    private fun CustomerDto.toEntity() = CustomerEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        aadhaar = aadhaar,
        address = address,
        photoUrl = photoUrl
    )
}
