package com.example.drivepulse.data.remote.dto

data class VehicleDto(
    val id: Long,
    val make: String,
    val model: String,
    val year: Int,
    val registrationNumber: String? = null,
    val currentMileage: Int = 0,
    val fuelType: String = "Petrol"
)