package com.example.drivepulse.data.model

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
