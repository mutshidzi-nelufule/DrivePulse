package com.example.drivepulse.ui.viewmodel

import com.example.drivepulse.data.model.MaintenanceRecord

data class MaintenanceUiState(
    val maintenanceLogs: List<MaintenanceRecord> = emptyList(),
    val totalCost: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
