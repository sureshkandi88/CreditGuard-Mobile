package com.kanditech.creditguard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // CREDIT, DEBIT
    val amount: Double,
    val date: Long,
    val reason: String? = null
)
