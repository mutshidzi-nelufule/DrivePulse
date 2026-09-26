package com.example.drivepulse.data.repository

import android.content.Context
import com.example.drivepulse.data.model.FuelRecord

class FuelRepository(context: Context) {

    private val fuelLogs = mutableListOf(
        FuelRecord(
            id = "f1",
            vehicleId = "v1",
            date = "2026-03-10",
            odometer = 47900,
            litres = 45.0,
            pricePerLitre = 23.50,
            totalCost = 1057.50,
            fuelStation = "Shell Ultra City"
        ),
        FuelRecord(
            id = "f2",
            vehicleId = "v1",
            date = "2026-03-24",
            odometer = 48500,
            litres = 46.5,
            pricePerLitre = 23.80,
            totalCost = 1106.70,
            fuelStation = "Engen QuickShop"
        )
    )

    fun getFuelRecords(vehicleId: String): List<FuelRecord> {
        return fuelLogs.filter { it.vehicleId == vehicleId }.sortedByDescending { it.odometer }
    }

    fun addFuelRecord(record: FuelRecord) {
        fuelLogs.add(record)
    }

    fun deleteFuelRecord(id: String) {
        fuelLogs.removeAll { it.id == id }
    }
}
