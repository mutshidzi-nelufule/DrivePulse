package com.example.drivepulse.ui.viewmodel

import com.example.drivepulse.data.model.Vehicle

data class VehicleUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicle: Vehicle? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
