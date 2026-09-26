package com.example.drivepulse.data.remote.dto

data class FuelRecordDto(
    val id: String,
    val vehicleId: String,
    val date: String,
    val odometer: Int,
    val litres: Double,
    val pricePerLitre: Double,
    val totalCost: Double,
    val fuelStation: String = "",
    val isFullTank: Boolean = true,
    val notes: String = ""
)
