package com.example.drivepulse.data.model

data class MaintenanceRecord(
    val id: String = "",
    val vehicleId: String = "",
    val serviceType: String = "",
    val date: String = "",
    val odometer: Int = 0,
    val cost: Double = 0.0,
    val description: String = "",
    val nextServiceOdometer: Int = 0,
    val isCompleted: Boolean = true
)
