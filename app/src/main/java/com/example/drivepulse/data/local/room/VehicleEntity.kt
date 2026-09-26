package com.example.drivepulse.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val make: String,
    val model: String,
    val year: Int,
    val registrationNumber: String,
    val vin: String,
    val fuelType: String,
    val currentOdometer: Int,
    val serviceIntervalKm: Int,
    val isDefault: Boolean,
    val createdAt: Long
)
