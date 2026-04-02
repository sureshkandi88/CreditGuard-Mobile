package com.kanditech.creditguard.domain.usecase

import com.kanditech.creditguard.data.local.entities.CreditorEntity
import com.kanditech.creditguard.domain.repository.CreditGuardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val repository: CreditGuardRepository
) {
    operator fun invoke(): Flow<CreditorEntity?> {
        return repository.getDashboardData()
    }

    suspend fun refresh(): Result<Unit> {
        return repository.refreshDashboard()
    }
}
