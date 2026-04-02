package com.kanditech.creditguard.domain.usecase

import com.kanditech.creditguard.data.local.entities.GroupEntity
import com.kanditech.creditguard.domain.repository.CreditGuardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupsUseCase @Inject constructor(
    private val repository: CreditGuardRepository
) {
    operator fun invoke(): Flow<List<GroupEntity>> {
        return repository.getGroups()
    }

    suspend fun refresh(): Result<Unit> {
        return repository.refreshGroups()
    }
}
