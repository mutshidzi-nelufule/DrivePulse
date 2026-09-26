package com.example.drivepulse

import com.example.drivepulse.data.local.Currency
import com.example.drivepulse.data.local.DistanceUnit
import com.example.drivepulse.data.local.FuelUnit
import com.example.drivepulse.data.local.SettingsPreferences
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsPreferencesTest {

    @Test
    fun testDefaultCurrencySymbol() {
        val settings = SettingsPreferences()
        assertEquals("R", settings.getCurrencySymbol())
        assertEquals("km", settings.getDistanceUnitLabel())
        assertEquals("L", settings.getFuelUnitLabel())
    }

    @Test
    fun testCurrencySymbolFormatting() {
        val usdSettings = SettingsPreferences(currency = Currency.USD)
        assertEquals("$", usdSettings.getCurrencySymbol())

        val eurSettings = SettingsPreferences(currency = Currency.EUR)
        assertEquals("€", eurSettings.getCurrencySymbol())

        val gbpSettings = SettingsPreferences(currency = Currency.GBP)
        assertEquals("£", gbpSettings.getCurrencySymbol())
    }

    @Test
    fun testUnitLabelFormatting() {
        val milesSettings = SettingsPreferences(distanceUnit = DistanceUnit.MILES, fuelUnit = FuelUnit.GALLONS)
        assertEquals("mi", milesSettings.getDistanceUnitLabel())
        assertEquals("gal", milesSettings.getFuelUnitLabel())
    }
}
