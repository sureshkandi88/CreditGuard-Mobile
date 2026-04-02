package com.kanditech.creditguard.data.remote.dto

data class CustomerDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val aadhaar: String,
    val address: String,
    val photoUrl: String?
)

data class CreateCustomerRequest(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val aadhaar: String,
    val address: String,
    val photoBase64: String? = null
)
