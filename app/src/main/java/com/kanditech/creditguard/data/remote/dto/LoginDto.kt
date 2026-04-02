package com.kanditech.creditguard.data.remote.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val firstName: String,
    val lastName: String
)
