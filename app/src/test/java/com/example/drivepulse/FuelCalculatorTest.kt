package com.example.drivepulse

import com.example.drivepulse.data.model.FuelRecord
import com.example.drivepulse.util.FuelCalculator
import com.example.drivepulse.util.FuelValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelCalculatorTest {

    @Test
    fun testTotalCostCalculation() {
        val cost = FuelCalculator.calculateTotalCost(litres = 45.0, pricePerLitre = 23.50)
        assertEquals(1057.50, cost, 0.001)
    }

    @Test
    fun testFuelEfficiencyCalculation() {
        val prevLog = FuelRecord(
            id = "f1", vehicleId = "v1", date = "2026-03-01",
            odometer = 40000, litres = 40.0, pricePerLitre = 23.00
        )
        val currentLog = FuelRecord(
            id = "f2", vehicleId = "v1", date = "2026-03-10",
            odometer = 40600, litres = 50.0, pricePerLitre = 23.00 // 600 km, 50 litres -> 8.33 L/100km
        )

        val efficiency = FuelCalculator.calculateEfficiency(currentLog, prevLog)
        assertEquals(8.333, efficiency!!, 0.005)
    }

    @Test
    fun testFuelEfficiencyWithNoPreviousRecord() {
        val currentLog = FuelRecord(
            id = "f1", vehicleId = "v1", date = "2026-03-01",
            odometer = 40000, litres = 45.0, pricePerLitre = 23.00
        )
        val efficiency = FuelCalculator.calculateEfficiency(currentLog, null)
        assertNull(efficiency)
    }

    @Test
    fun testFuelEfficiencyWithZeroDistance() {
        val prevLog = FuelRecord(
            id = "f1", vehicleId = "v1", date = "2026-03-01",
            odometer = 40000, litres = 40.0, pricePerLitre = 23.00
        )
        val currentLog = FuelRecord(
            id = "f2", vehicleId = "v1", date = "2026-03-02",
            odometer = 40000, litres = 20.0, pricePerLitre = 23.00 // Zero km distance
        )
        val efficiency = FuelCalculator.calculateEfficiency(currentLog, prevLog)
        assertNull(efficiency) // Division by zero safely prevented
    }

    @Test
    fun testFuelEfficiencyWithNegativeDistance() {
        val prevLog = FuelRecord(
            id = "f1", vehicleId = "v1", date = "2026-03-01",
            odometer = 40000, litres = 40.0, pricePerLitre = 23.00
        )
        val currentLog = FuelRecord(
            id = "f2", vehicleId = "v1", date = "2026-03-02",
            odometer = 39500, litres = 20.0, pricePerLitre = 23.00 // Invalid negative distance
        )
        val efficiency = FuelCalculator.calculateEfficiency(currentLog, prevLog)
        assertNull(efficiency)
    }

    @Test
    fun testAverageConsumptionWithMultipleLogs() {
        val logs = listOf(
            FuelRecord("f1", "v1", "2026-03-01", 40000, 40.0, 23.00),
            FuelRecord("f2", "v1", "2026-03-10", 40600, 48.0, 23.00),
            FuelRecord("f3", "v1", "2026-03-20", 41200, 48.0, 23.00)
        )
        val avg = FuelCalculator.calculateAverageConsumption(logs)
        assertEquals(8.0, avg, 0.01) // (96 L / 1200 km) * 100 = 8.0 L/100km
    }

    @Test
    fun testFuelValidationRules() {
        val valid = FuelCalculator.validateFuelInput("45000", "40.0", "23.50", previousOdometer = 44000)
        assertTrue(valid is FuelValidationResult.Success)

        val invalidOdometer = FuelCalculator.validateFuelInput("43000", "40.0", "23.50", previousOdometer = 44000)
        assertTrue(invalidOdometer is FuelValidationResult.Error)

        val invalidLitres = FuelCalculator.validateFuelInput("45000", "0.0", "23.50")
        assertTrue(invalidLitres is FuelValidationResult.Error)
    }
}
