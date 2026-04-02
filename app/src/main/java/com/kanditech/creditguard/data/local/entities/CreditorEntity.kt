package com.kanditech.creditguard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creditor")
data class CreditorEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val walletBalance: Double = 0.0,
    val todayCollection: Double = 0.0,
    val expectedCollectionToday: Double = 0.0,
    val activeGroupsCount: Int = 0
)
