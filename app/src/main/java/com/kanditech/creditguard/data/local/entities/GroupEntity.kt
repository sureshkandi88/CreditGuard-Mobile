package com.kanditech.creditguard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val leaderId: String,
    val leaderName: String,
    val location: String,
    val photoUrl: String? = null,
    val isActive: Boolean = true,
    val outstandingAmount: Double = 0.0,
    val dailyInstallment: Double = 0.0,
    val totalRepaidPercent: Float = 0f
)
