package com.example.drivepulse.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val title: String,
    val description: String,
    val dueOdometer: Int,
    val dueDate: String,
    val isCompleted: Boolean
)
