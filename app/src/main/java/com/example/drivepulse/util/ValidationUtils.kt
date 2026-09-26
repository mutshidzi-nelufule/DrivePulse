package com.example.drivepulse.util

import java.security.MessageDigest
import java.util.Calendar

object ValidationUtils {

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return email.trim().matches(emailRegex)
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 6) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    fun validateVehicle(
        make: String,
        model: String,
        yearText: String = "",
        registrationNumber: String = "",
        odometerText: String = "",
        vin: String = ""
    ): VehicleValidationResult {
        val trimmedMake = make.trim()
        val trimmedModel = model.trim()
        val trimmedVin = vin.trim()

        if (trimmedMake.isEmpty()) {
            return VehicleValidationResult.Error("Vehicle make cannot be empty.")
        }
        if (trimmedModel.isEmpty()) {
            return VehicleValidationResult.Error("Vehicle model cannot be empty.")
        }

        if (yearText.isNotBlank()) {
            val year = yearText.toIntOrNull()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (year == null || year < 1900 || year > currentYear + 1) {
                return VehicleValidationResult.Error("Please enter a valid model year (between 1900 and ${currentYear + 1}).")
            }
        }

        if (odometerText.isNotBlank()) {
            val odometer = odometerText.toIntOrNull()
            if (odometer == null || odometer < 0) {
                return VehicleValidationResult.Error("Current odometer reading cannot be negative.")
            }
        }

        if (trimmedVin.isNotEmpty()) {
            if (trimmedVin.length > 17 || !trimmedVin.all { it.isLetterOrDigit() }) {
                return VehicleValidationResult.Error("VIN must be up to 17 alphanumeric characters.")
            }
        }

        return VehicleValidationResult.Success
    }
}
