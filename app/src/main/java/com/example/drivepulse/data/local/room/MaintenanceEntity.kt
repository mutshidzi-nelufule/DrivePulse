package com.example.drivepulse.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_records")
data class MaintenanceEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val serviceType: String,
    val date: String,
    val odometer: Int,
    val cost: Double,
    val description: String,
    val nextServiceOdometer: Int,
    val isCompleted: Boolean
)
