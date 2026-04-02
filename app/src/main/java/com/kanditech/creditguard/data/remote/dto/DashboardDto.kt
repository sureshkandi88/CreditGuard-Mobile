package com.kanditech.creditguard.data.remote.dto

data class DashboardDto(
    val walletBalance: Double,
    val todayCollection: Double,
    val expectedCollectionToday: Double,
    val activeGroupsCount: Int
)
