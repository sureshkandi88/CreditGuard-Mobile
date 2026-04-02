package com.kanditech.creditguard.domain.usecase

import com.kanditech.creditguard.data.local.entities.WalletTransactionEntity
import com.kanditech.creditguard.domain.repository.CreditGuardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWalletTransactionsUseCase @Inject constructor(
    private val repository: CreditGuardRepository
) {
    operator fun invoke(): Flow<List<WalletTransactionEntity>> {
        return repository.getWalletTransactions()
    }

    suspend fun refresh(): Result<Unit> {
        return repository.refreshWallet()
    }

    suspend fun addMoney(amount: Double, reason: String): Result<Unit> {
        return repository.addMoneyToWallet(amount, reason)
    }
}
