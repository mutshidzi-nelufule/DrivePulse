package com.example.drivepulse.data.remote.dto

data class MaintenanceRecordDto(
    val id: String,
    val vehicleId: String,
    val serviceType: String,
    val date: String,
    val odometer: Int,
    val cost: Double,
    val description: String = "",
    val nextServiceOdometer: Int = 0,
    val isCompleted: Boolean = true
)
