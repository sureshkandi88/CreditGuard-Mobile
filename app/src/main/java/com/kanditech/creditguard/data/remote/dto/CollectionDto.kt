package com.kanditech.creditguard.data.remote.dto

data class CollectionRequest(
    val groupId: String,
    val amount: Double,
    val date: Long,
    val notes: String? = null
)
