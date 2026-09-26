package com.example.drivepulse.util

sealed class MaintenanceValidationResult {
    object Success : MaintenanceValidationResult()
    data class Error(val message: String) : MaintenanceValidationResult()
}

object MaintenanceValidator {

    /**
     * Pre-defined standard service categories.
     */
    val SERVICE_CATEGORIES = listOf(
        "Oil Change",
        "Tyre Service",
        "Brake Service",
        "Battery Replacement",
        "Air Filter",
        "General Service",
        "Repair",
        "Other"
    )

    /**
     * Validates maintenance log entry fields.
     */
    fun validateMaintenance(
        serviceType: String,
        costText: String,
        odometerText: String,
        nextOdometerText: String = ""
    ): MaintenanceValidationResult {
        val trimmedType = serviceType.trim()
        if (trimmedType.isEmpty()) {
            return MaintenanceValidationResult.Error("Please select or enter a service type.")
        }

        val cost = costText.toDoubleOrNull()
        if (cost == null || cost < 0) {
            return MaintenanceValidationResult.Error("Service cost must be a valid non-negative amount.")
        }

        val odo = odometerText.toIntOrNull()
        if (odo == null || odo < 0) {
            return MaintenanceValidationResult.Error("Odometer reading must be a positive number.")
        }

        if (nextOdometerText.isNotBlank()) {
            val nextOdo = nextOdometerText.toIntOrNull()
            if (nextOdo == null || nextOdo <= odo) {
                return MaintenanceValidationResult.Error("Next service odometer ($nextOdo km) must be greater than current service odometer ($odo km).")
            }
        }

        return MaintenanceValidationResult.Success
    }
}
