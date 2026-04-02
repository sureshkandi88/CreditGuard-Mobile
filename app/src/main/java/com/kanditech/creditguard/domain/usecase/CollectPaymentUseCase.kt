package com.kanditech.creditguard.domain.usecase

import com.kanditech.creditguard.domain.repository.CreditGuardRepository
import javax.inject.Inject

class CollectPaymentUseCase @Inject constructor(
    private val repository: CreditGuardRepository
) {
    suspend operator fun invoke(groupId: String, amount: Double, notes: String?): Result<Unit> {
        return repository.collectPayment(groupId, amount, notes)
    }
}
