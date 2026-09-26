package com.example.drivepulse.data.model

data class Reminder(
    val id: String = "",
    val vehicleId: String = "",
    val title: String = "",
    val description: String = "",
    val dueOdometer: Int = 0,
    val dueDate: String = "",
    val isCompleted: Boolean = false
)
