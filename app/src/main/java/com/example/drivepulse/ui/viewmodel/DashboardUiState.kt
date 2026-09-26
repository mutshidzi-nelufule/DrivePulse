package com.example.drivepulse.ui.viewmodel

import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.util.HealthScoreResult

data class DashboardUiState(
    val selectedVehicle: Vehicle? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val healthScore: HealthScoreResult? = null,
    val avgFuelConsumption: Double = 0.0,
    val totalFuelSpending: Double = 0.0,
    val totalMaintenanceSpending: Double = 0.0,
    val isQuickAddFuelOpen: Boolean = false,
    val isQuickAddMaintenanceOpen: Boolean = false
)
