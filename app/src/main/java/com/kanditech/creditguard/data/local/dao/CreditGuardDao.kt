package com.kanditech.creditguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kanditech.creditguard.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditGuardDao {

    // Creditor
    @Query("SELECT * FROM creditor LIMIT 1")
    fun getCreditor(): Flow<CreditorEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditor(creditor: CreditorEntity)

    // Groups
    @Query("SELECT * FROM groups")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE isActive = 1")
    fun getActiveGroups(): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: String): GroupEntity?

    // Collections
    @Query("SELECT * FROM collections WHERE groupId = :groupId ORDER BY date DESC")
    fun getCollectionsByGroup(groupId: String): Flow<List<CollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<CollectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity)

    // Pending Collections (for offline sync)
    @Query("SELECT * FROM pending_collections ORDER BY date ASC")
    suspend fun getPendingCollections(): List<PendingCollectionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingCollection(pendingCollection: PendingCollectionEntity)

    @Query("DELETE FROM pending_collections WHERE id = :id")
    suspend fun deletePendingCollection(id: Int)

    // Wallet
    @Query("SELECT * FROM wallet_transactions ORDER BY date DESC")
    fun getWalletTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletTransactions(transactions: List<WalletTransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletTransaction(transaction: WalletTransactionEntity)

    // Credit Details
    @Query("SELECT * FROM credit_details WHERE groupId = :groupId")
    suspend fun getCreditDetailsByGroup(groupId: String): CreditDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditDetails(creditDetails: CreditDetailsEntity)

    // Customers
    @Query("SELECT * FROM customers")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE phone = :phone OR aadhaar = :aadhaar")
    suspend fun findCustomer(phone: String, aadhaar: String): CustomerEntity?
}
