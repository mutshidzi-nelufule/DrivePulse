package com.example.drivepulse.util

import com.example.drivepulse.data.model.FuelRecord
import com.example.drivepulse.data.model.MaintenanceRecord
import com.example.drivepulse.data.model.Vehicle

data class ReportSummary(
    val totalFuelSpent: Double,
    val totalMaintenanceSpent: Double,
    val totalExpenses: Double,
    val avgFuelConsumption: Double,
    val costPerKilometre: Double,
    val totalDistanceDriven: Double,
    val fuelLogCount: Int,
    val maintenanceLogCount: Int,
    val fuelPercentage: Float,
    val maintenancePercentage: Float
)

object ReportCalculator {

    /**
     * Calculates Cost Per Kilometre driven.
     * Formula: Total Vehicle Expenses / Total Distance Driven (km)
     * Handles division by zero safely.
     */
    fun calculateCostPerKilometre(totalExpenses: Double, totalDistanceKm: Double): Double {
        if (totalDistanceKm <= 0 || totalExpenses <= 0) return 0.0
        return totalExpenses / totalDistanceKm
    }

    /**
     * Compiles complete vehicle financial and running report metrics.
     */
    fun generateReportSummary(
        vehicle: Vehicle?,
        fuelRecords: List<FuelRecord>,
        maintenanceRecords: List<MaintenanceRecord>
    ): ReportSummary {
        val fuelSpent = fuelRecords.sumOf { it.totalCost }
        val maintSpent = maintenanceRecords.sumOf { it.cost }
        val totalExpenses = fuelSpent + maintSpent

        val avgConsumption = FuelCalculator.calculateAverageConsumption(fuelRecords)

        // Calculate total distance tracked across fuel logs
        val sortedFuel = fuelRecords.sortedBy { it.odometer }
        val totalDistance = if (sortedFuel.size >= 2) {
            (sortedFuel.last().odometer - sortedFuel.first().odometer).toDouble()
        } else 0.0

        val costPerKm = calculateCostPerKilometre(totalExpenses, totalDistance)

        val fuelPct = if (totalExpenses > 0) (fuelSpent / totalExpenses).toFloat() else 0.5f
        val maintPct = if (totalExpenses > 0) (maintSpent / totalExpenses).toFloat() else 0.5f

        return ReportSummary(
            totalFuelSpent = fuelSpent,
            totalMaintenanceSpent = maintSpent,
            totalExpenses = totalExpenses,
            avgFuelConsumption = avgConsumption,
            costPerKilometre = costPerKm,
            totalDistanceDriven = totalDistance,
            fuelLogCount = fuelRecords.size,
            maintenanceLogCount = maintenanceRecords.size,
            fuelPercentage = fuelPct,
            maintenancePercentage = maintPct
        )
    }
}
