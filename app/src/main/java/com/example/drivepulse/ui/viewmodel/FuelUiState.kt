package com.example.drivepulse.ui.viewmodel

import com.example.drivepulse.data.model.FuelRecord

data class FuelUiState(
    val fuelLogs: List<FuelRecord> = emptyList(),
    val averageConsumption: Double = 0.0,
    val totalSpent: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
