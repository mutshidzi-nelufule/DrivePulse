package com.example.drivepulse

import com.example.drivepulse.util.MaintenanceValidationResult
import com.example.drivepulse.util.MaintenanceValidator
import org.junit.Assert.assertTrue
import org.junit.Test

class MaintenanceValidatorTest {

    @Test
    fun testValidMaintenanceData() {
        val result = MaintenanceValidator.validateMaintenance(
            serviceType = "Oil Change",
            costText = "1450.00",
            odometerText = "45000",
            nextOdometerText = "60000"
        )
        assertTrue(result is MaintenanceValidationResult.Success)
    }

    @Test
    fun testEmptyServiceType() {
        val result = MaintenanceValidator.validateMaintenance(
            serviceType = "",
            costText = "500.00",
            odometerText = "30000"
        )
        assertTrue(result is MaintenanceValidationResult.Error)
        assertTrue((result as MaintenanceValidationResult.Error).message.contains("select or enter a service type"))
    }

    @Test
    fun testNegativeCost() {
        val result = MaintenanceValidator.validateMaintenance(
            serviceType = "Brake Service",
            costText = "-200.00",
            odometerText = "30000"
        )
        assertTrue(result is MaintenanceValidationResult.Error)
        assertTrue((result as MaintenanceValidationResult.Error).message.contains("non-negative amount"))
    }

    @Test
    fun testNegativeOdometer() {
        val result = MaintenanceValidator.validateMaintenance(
            serviceType = "Tyre Rotation",
            costText = "800.00",
            odometerText = "-100"
        )
        assertTrue(result is MaintenanceValidationResult.Error)
        assertTrue((result as MaintenanceValidationResult.Error).message.contains("positive number"))
    }

    @Test
    fun testInvalidNextOdometer() {
        val result = MaintenanceValidator.validateMaintenance(
            serviceType = "General Service",
            costText = "2500.00",
            odometerText = "50000",
            nextOdometerText = "45000" // Next odometer smaller than current odometer
        )
        assertTrue(result is MaintenanceValidationResult.Error)
        assertTrue((result as MaintenanceValidationResult.Error).message.contains("must be greater than current"))
    }
}
