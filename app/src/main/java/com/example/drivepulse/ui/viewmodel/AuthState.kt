package com.example.drivepulse.ui.viewmodel

import com.example.drivepulse.data.model.User

data class AuthState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
