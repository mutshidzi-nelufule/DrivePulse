package com.example.drivepulse

import com.example.drivepulse.data.model.MaintenanceRecord
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.util.HealthScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthScoreCalculatorTest {

    @Test
    fun testHealthScoreWhenVehicleNull() {
        val result = HealthScoreCalculator.calculateHealthScore(null, emptyList())
        assertEquals(100, result.score)
        assertEquals("No Vehicle", result.statusText)
    }

    @Test
    fun testHealthScoreWhenNoMaintenance() {
        val vehicle = Vehicle(
            id = "v1",
            userId = "u1",
            make = "Toyota",
            model = "Corolla",
            year = 2022,
            registrationNumber = "AB 12 CD GP",
            currentOdometer = 45000
        )
        val result = HealthScoreCalculator.calculateHealthScore(vehicle, emptyList())
        assertEquals(85, result.score)
        assertEquals("Excellent", result.statusText)
    }

    @Test
    fun testHealthScoreWithUpToDateMaintenance() {
        val vehicle = Vehicle(
            id = "v1",
            userId = "u1",
            make = "Toyota",
            model = "Corolla",
            year = 2022,
            registrationNumber = "AB 12 CD GP",
            currentOdometer = 45000
        )
        val maintenance = listOf(
            MaintenanceRecord(
                id = "m1",
                vehicleId = "v1",
                serviceType = "Oil Change",
                date = "2026-01-10",
                odometer = 40000,
                cost = 1200.0,
                nextServiceOdometer = 55000
            )
        )
        val result = HealthScoreCalculator.calculateHealthScore(vehicle, maintenance)
        assertEquals(100, result.score)
        assertEquals("Excellent", result.statusText)
    }

    @Test
    fun testHealthScoreWithOverdueMaintenance() {
        val vehicle = Vehicle(
            id = "v1",
            userId = "u1",
            make = "Toyota",
            model = "Corolla",
            year = 2022,
            registrationNumber = "AB 12 CD GP",
            currentOdometer = 56000
        )
        val maintenance = listOf(
            MaintenanceRecord(
                id = "m1",
                vehicleId = "v1",
                serviceType = "Oil Change",
                date = "2025-06-10",
                odometer = 40000,
                cost = 1200.0,
                nextServiceOdometer = 55000 // Overdue by 1000 km
            )
        )
        val result = HealthScoreCalculator.calculateHealthScore(vehicle, maintenance)
        assertEquals(80, result.score)
        assertEquals("Good", result.statusText)
        assertTrue(result.recommendations.any { it.contains("Overdue service") })
    }

    @Test
    fun testHealthScoreWithUpcomingServiceSoon() {
        val vehicle = Vehicle(
            id = "v1",
            userId = "u1",
            make = "Toyota",
            model = "Corolla",
            year = 2022,
            registrationNumber = "AB 12 CD GP",
            currentOdometer = 54700
        )
        val maintenance = listOf(
            MaintenanceRecord(
                id = "m1",
                vehicleId = "v1",
                serviceType = "Oil Change",
                date = "2025-06-10",
                odometer = 40000,
                cost = 1200.0,
                nextServiceOdometer = 55000 // 300 km remaining
            )
        )
        val result = HealthScoreCalculator.calculateHealthScore(vehicle, maintenance)
        assertEquals(90, result.score)
        assertEquals("Excellent", result.statusText)
        assertTrue(result.recommendations.any { it.contains("Upcoming service due soon") })
    }
}
