package com.example.drivepulse

import com.example.drivepulse.data.model.FuelRecord
import com.example.drivepulse.data.model.MaintenanceRecord
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.util.ReportCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportCalculatorTest {

    @Test
    fun testCostPerKilometreCalculation() {
        val costPerKm = ReportCalculator.calculateCostPerKilometre(totalExpenses = 3000.0, totalDistanceKm = 1000.0)
        assertEquals(3.00, costPerKm, 0.001) // R 3.00 / km
    }

    @Test
    fun testCostPerKilometreWithZeroDistance() {
        val costPerKm = ReportCalculator.calculateCostPerKilometre(totalExpenses = 3000.0, totalDistanceKm = 0.0)
        assertEquals(0.00, costPerKm, 0.001) // Division by zero safely prevented
    }

    @Test
    fun testGenerateReportSummaryWithRecords() {
        val vehicle = Vehicle("v1", "u1", "Toyota", "Corolla", 2022, "ND 123", currentOdometer = 48000)

        val fuelLogs = listOf(
            FuelRecord("f1", "v1", "2026-03-01", 40000, 40.0, 25.00, 1000.0),
            FuelRecord("f2", "v1", "2026-03-15", 41000, 50.0, 25.00, 1250.0) // 1000 km distance, R2250 fuel
        )

        val maintLogs = listOf(
            MaintenanceRecord("m1", "v1", "Oil Change", "2026-03-05", 40500, 750.0) // R750 maintenance
        )

        val report = ReportCalculator.generateReportSummary(vehicle, fuelLogs, maintLogs)

        assertEquals(2250.0, report.totalFuelSpent, 0.01)
        assertEquals(750.0, report.totalMaintenanceSpent, 0.01)
        assertEquals(3000.0, report.totalExpenses, 0.01) // Total = R 3000
        assertEquals(1000.0, report.totalDistanceDriven, 0.01) // Distance = 1000 km
        assertEquals(3.00, report.costPerKilometre, 0.01) // R 3.00 / km
        assertEquals(0.75f, report.fuelPercentage, 0.01f) // 75% fuel
        assertEquals(0.25f, report.maintenancePercentage, 0.01f) // 25% maintenance
    }

    @Test
    fun testGenerateReportSummaryWithEmptyRecords() {
        val vehicle = Vehicle("v1", "u1", "Toyota", "Corolla", 2022, "ND 123")
        val report = ReportCalculator.generateReportSummary(vehicle, emptyList(), emptyList())

        assertEquals(0.0, report.totalExpenses, 0.01)
        assertEquals(0.0, report.costPerKilometre, 0.01)
        assertEquals(0.0, report.avgFuelConsumption, 0.01)
    }
}
