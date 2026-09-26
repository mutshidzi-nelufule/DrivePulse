package com.example.drivepulse.data.model

data class FuelRecord(
    val id: String = "",
    val vehicleId: String = "",
    val date: String = "",
    val odometer: Int = 0,
    val litres: Double = 0.0,
    val pricePerLitre: Double = 0.0,
    val totalCost: Double = litres * pricePerLitre,
    val fuelType: String = "Petrol",
    val fuelStation: String = "",
    val isFullTank: Boolean = true,
    val notes: String = ""
)
