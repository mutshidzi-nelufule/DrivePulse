package com.example.drivepulse.data.model

data class Vehicle(
    val id: String = "",
    val userId: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int = 2024,
    val registrationNumber: String = "",
    val vin: String = "",
    val fuelType: String = "Petrol",
    val currentOdometer: Int = 0,
    val serviceIntervalKm: Int = 15000,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getDisplayName(): String = "$year $make $model ($registrationNumber)"
}
