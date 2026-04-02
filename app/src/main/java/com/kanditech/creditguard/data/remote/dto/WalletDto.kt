package com.kanditech.creditguard.data.remote.dto

data class WalletTransactionDto(
    val id: String,
    val type: String,
    val amount: Double,
    val date: Long,
    val reason: String?
)

data class AddMoneyRequest(
    val amount: Double,
    val reason: String
)
