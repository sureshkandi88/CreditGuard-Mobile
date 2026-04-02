package com.kanditech.creditguard.data.remote.dto

data class GroupDto(
    val id: String,
    val name: String,
    val leaderId: String,
    val leaderName: String,
    val location: String,
    val photoUrl: String?,
    val isActive: Boolean,
    val outstandingAmount: Double,
    val dailyInstallment: Double,
    val totalRepaidPercent: Float
)

data class CreateGroupRequest(
    val name: String,
    val location: String,
    val leaderId: String,
    val members: List<GroupMemberRequest>
)

data class GroupMemberRequest(
    val customerId: String,
    val ratioPercent: Float
)
