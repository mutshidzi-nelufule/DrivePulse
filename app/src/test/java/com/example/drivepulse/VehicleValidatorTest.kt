package com.example.drivepulse

import com.example.drivepulse.util.VehicleValidationResult
import com.example.drivepulse.util.ValidationUtils
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleValidatorTest {

    @Test
    fun testValidVehicleData() {
        val result = ValidationUtils.validateVehicle(
            make = "Toyota",
            model = "Hilux 2.8 GD-6",
            yearText = "2023",
            registrationNumber = "ND 888-999",
            odometerText = "35000",
            vin = "AHTFX349830219482"
        )
        assertTrue(result is VehicleValidationResult.Success)
    }

    @Test
    fun testEmptyMake() {
        val result = ValidationUtils.validateVehicle(
            make = "",
            model = "Corolla",
            yearText = "2021",
            registrationNumber = "AB 12 CD GP",
            odometerText = "12000"
        )
        assertTrue(result is VehicleValidationResult.Error)
        assertTrue((result as VehicleValidationResult.Error).message.contains("make cannot be empty"))
    }

    @Test
    fun testInvalidYear() {
        val result = ValidationUtils.validateVehicle(
            make = "Ford",
            model = "Ranger",
            yearText = "1850",
            registrationNumber = "CA 12345",
            odometerText = "50000"
        )
        assertTrue(result is VehicleValidationResult.Error)
        assertTrue((result as VehicleValidationResult.Error).message.contains("valid model year"))
    }

    @Test
    fun testNegativeOdometer() {
        val result = ValidationUtils.validateVehicle(
            make = "BMW",
            model = "320i",
            yearText = "2020",
            registrationNumber = "GP 123",
            odometerText = "-500"
        )
        assertTrue(result is VehicleValidationResult.Error)
        assertTrue((result as VehicleValidationResult.Error).message.contains("cannot be negative"))
    }

    @Test
    fun testInvalidVin() {
        val result = ValidationUtils.validateVehicle(
            make = "Audi",
            model = "A4",
            yearText = "2019",
            registrationNumber = "KZN 001",
            odometerText = "60000",
            vin = "INVALID-VIN-WITH-SPECIAL-CHARS!!!"
        )
        assertTrue(result is VehicleValidationResult.Error)
        assertTrue((result as VehicleValidationResult.Error).message.contains("17 alphanumeric"))
    }
}
