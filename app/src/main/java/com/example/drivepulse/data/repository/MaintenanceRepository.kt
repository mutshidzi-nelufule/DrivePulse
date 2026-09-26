package com.example.drivepulse.data.repository

import android.content.Context
import com.example.drivepulse.data.model.MaintenanceRecord

class MaintenanceRepository(context: Context) {

    private val maintenanceLogs = mutableListOf(
        MaintenanceRecord(
            id = "m1",
            vehicleId = "v1",
            serviceType = "Oil & Filter Change",
            date = "2026-01-15",
            odometer = 45000,
            cost = 1450.0,
            description = "Synthetic engine oil replacement",
            nextServiceOdometer = 60000
        )
    )

    fun getMaintenanceRecords(vehicleId: String): List<MaintenanceRecord> {
        return maintenanceLogs.filter { it.vehicleId == vehicleId }.sortedByDescending { it.odometer }
    }

    fun addMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceLogs.add(record)
    }

    fun deleteMaintenanceRecord(id: String) {
        maintenanceLogs.removeAll { it.id == id }
    }
}
