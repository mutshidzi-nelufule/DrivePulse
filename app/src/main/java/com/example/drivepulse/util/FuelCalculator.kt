package com.example.drivepulse.util

import com.example.drivepulse.data.model.FuelRecord

sealed class FuelValidationResult {
    object Success : FuelValidationResult()
    data class Error(val message: String) : FuelValidationResult()
}

object FuelCalculator {

    /**
     * Calculates total cost of a fuel fill-up.
     */
    fun calculateTotalCost(litres: Double, pricePerLitre: Double): Double {
        if (litres <= 0 || pricePerLitre <= 0) return 0.0
        return litres * pricePerLitre
    }

    /**
     * Validates fuel entry inputs.
     */
    fun validateFuelInput(
        odometerText: String,
        litresText: String,
        pricePerLitreText: String,
        previousOdometer: Int? = null
    ): FuelValidationResult {
        val odo = odometerText.toIntOrNull()
        if (odo == null || odo < 0) {
            return FuelValidationResult.Error("Odometer reading must be a positive number.")
        }

        if (previousOdometer != null && odo < previousOdometer) {
            return FuelValidationResult.Error("New odometer ($odo km) cannot be less than previous reading ($previousOdometer km).")
        }

        val litres = litresText.toDoubleOrNull()
        if (litres == null || litres <= 0) {
            return FuelValidationResult.Error("Fuel volume (litres) must be greater than zero.")
        }

        val price = pricePerLitreText.toDoubleOrNull()
        if (price == null || price < 0) {
            return FuelValidationResult.Error("Price per litre must be a non-negative number.")
        }

        return FuelValidationResult.Success
    }

    /**
     * Calculates fuel efficiency between two fill-ups in L/100km.
     * Formula: (Litres Filled / Distance Travelled in km) * 100
     * Handles edge cases: no previous record, zero distance, division by zero, negative distance.
     */
    fun calculateEfficiency(currentLog: FuelRecord, previousLog: FuelRecord?): Double? {
        if (previousLog == null) return null // Edge case: No previous record to calculate distance

        val distanceKm = currentLog.odometer - previousLog.odometer
        if (distanceKm <= 0) return null // Edge case: Zero or negative distance

        if (currentLog.litres <= 0) return null // Edge case: Missing or zero fuel amount

        return (currentLog.litres / distanceKm) * 100.0
    }

    /**
     * Calculates overall average fuel consumption across a history of fuel fill-ups.
     */
    fun calculateAverageConsumption(fuelRecords: List<FuelRecord>): Double {
        val sortedLogs = fuelRecords.sortedBy { it.odometer }
        if (sortedLogs.size < 2) return 0.0

        val totalLitres = sortedLogs.drop(1).sumOf { it.litres }
        val totalDistanceKm = sortedLogs.last().odometer - sortedLogs.first().odometer

        if (totalDistanceKm <= 0 || totalLitres <= 0) return 0.0

        return (totalLitres / totalDistanceKm) * 100.0
    }
}
