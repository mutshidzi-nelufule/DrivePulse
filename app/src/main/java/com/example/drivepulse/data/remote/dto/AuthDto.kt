package com.example.drivepulse.data.remote.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponseDto(
    val id: Long,
    val name: String,
    val email: String,
    val password: String?
)
