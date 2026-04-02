package com.kanditech.creditguard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val amount: Double,
    val date: Long,
    val collectorId: String,
    val collectorName: String,
    val notes: String? = null
)
