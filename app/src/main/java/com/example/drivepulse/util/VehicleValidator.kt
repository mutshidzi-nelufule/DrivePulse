package com.example.drivepulse.util

import java.util.Calendar

sealed class VehicleValidationResult {
    object Success : VehicleValidationResult()
    data class Error(val message: String) : VehicleValidationResult()
}

object VehicleValidator {

    /**
     * Validates vehicle input details before saving.
     */
    fun validateVehicle(
        make: String,
        model: String,
        yearText: String,
        registrationNumber: String,
        odometerText: String,
        vin: String = ""
    ): VehicleValidationResult {
        val trimmedMake = make.trim()
        val trimmedModel = model.trim()
        val trimmedReg = registrationNumber.trim()
        val trimmedVin = vin.trim()

        if (trimmedMake.isEmpty()) {
            return VehicleValidationResult.Error("Vehicle make cannot be empty.")
        }
        if (trimmedModel.isEmpty()) {
            return VehicleValidationResult.Error("Vehicle model cannot be empty.")
        }

        val year = yearText.toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (year == null || year < 1900 || year > currentYear + 1) {
            return VehicleValidationResult.Error("Please enter a valid model year (between 1900 and ${currentYear + 1}).")
        }

        if (trimmedReg.isEmpty() || trimmedReg.length < 2) {
            return VehicleValidationResult.Error("Please enter a valid registration number.")
        }

        val odometer = odometerText.toIntOrNull()
        if (odometer == null || odometer < 0) {
            return VehicleValidationResult.Error("Current odometer reading cannot be negative.")
        }

        if (trimmedVin.isNotEmpty()) {
            if (trimmedVin.length > 17 || !trimmedVin.all { it.isLetterOrDigit() }) {
                return VehicleValidationResult.Error("VIN must be up to 17 alphanumeric characters.")
            }
        }

        return VehicleValidationResult.Success
    }
}
