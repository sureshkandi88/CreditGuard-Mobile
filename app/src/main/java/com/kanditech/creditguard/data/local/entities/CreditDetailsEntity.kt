package com.kanditech.creditguard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_details")
data class CreditDetailsEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val principalAmount: Double,
    val interestAmount: Double,
    val dailyInstallment: Double,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true
)
