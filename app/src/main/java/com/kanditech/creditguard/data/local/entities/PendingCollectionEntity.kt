package com.kanditech.creditguard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_collections")
data class PendingCollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: String,
    val amount: Double,
    val date: Long,
    val collectorId: String,
    val notes: String? = null
)
