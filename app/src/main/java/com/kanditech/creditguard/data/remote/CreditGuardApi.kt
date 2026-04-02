package com.kanditech.creditguard.data.remote

import com.kanditech.creditguard.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CreditGuardApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("creditor/dashboard")
    suspend fun getDashboard(): Response<DashboardDto>

    @GET("groups")
    suspend fun getGroups(): Response<List<GroupDto>>

    @GET("groups/{id}")
    suspend fun getGroupDetails(@Path("id") id: String): Response<GroupDto>

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<GroupDto>

    @POST("collections")
    suspend fun collectPayment(@Body request: CollectionRequest): Response<Unit>

    @POST("collections/sync")
    suspend fun syncCollections(@Body collections: List<CollectionRequest>): Response<Unit>

    @GET("customers")
    suspend fun getCustomers(): Response<List<CustomerDto>>

    @POST("customers")
    suspend fun createCustomer(@Body request: CreateCustomerRequest): Response<CustomerDto>

    @GET("wallet/transactions")
    suspend fun getWalletTransactions(): Response<List<WalletTransactionDto>>

    @POST("wallet/add-money")
    suspend fun addMoneyToWallet(@Body request: AddMoneyRequest): Response<Unit>
}
