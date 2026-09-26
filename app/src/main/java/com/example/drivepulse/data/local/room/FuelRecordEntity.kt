package com.example.drivepulse.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_records")
data class FuelRecordEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val date: String,
    val odometer: Int,
    val litres: Double,
    val pricePerLitre: Double,
    val totalCost: Double,
    val fuelType: String,
    val fuelStation: String,
    val isFullTank: Boolean,
    val notes: String
)
