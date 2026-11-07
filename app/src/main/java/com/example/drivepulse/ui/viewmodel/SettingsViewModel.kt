package com.example.drivepulse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.drivepulse.data.local.Currency
import com.example.drivepulse.data.local.DistanceUnit
import com.example.drivepulse.data.local.FuelUnit
import com.example.drivepulse.data.local.SettingsManager
import com.example.drivepulse.data.local.SettingsPreferences
import com.example.drivepulse.data.local.ThemeMode
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.data.repository.VehicleRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)
    private val vehicleRepository = VehicleRepository.getInstance(application)

    var settings by mutableStateOf(settingsManager.getSettings())
        private set

    var vehicles by mutableStateOf<List<Vehicle>>(emptyList())
        private set

    init {
        loadSettingsData()
    }

    fun loadSettingsData() {
        settings = settingsManager.getSettings()
        vehicles = vehicleRepository.getVehicles()
    }

    fun updateTheme(themeMode: ThemeMode) {
        val updated = settings.copy(themeMode = themeMode)
        settings = updated
        settingsManager.saveSettings(updated)
    }

    fun updateDistanceUnit(unit: DistanceUnit) {
        val updated = settings.copy(distanceUnit = unit)
        settings = updated
        settingsManager.saveSettings(updated)
    }

    fun updateFuelUnit(unit: FuelUnit) {
        val updated = settings.copy(fuelUnit = unit)
        settings = updated
        settingsManager.saveSettings(updated)
    }

    fun updateCurrency(currency: Currency) {
        val updated = settings.copy(currency = currency)
        settings = updated
        settingsManager.saveSettings(updated)
    }

    fun updateNotifications(enabled: Boolean) {
        val updated = settings.copy(notificationsEnabled = enabled)
        settings = updated
        settingsManager.saveSettings(updated)
    }

    fun updateLanguage(language: String) {
        val updated = settings.copy(language = language)
        settings = updated
        settingsManager.saveSettings(updated)
        com.example.drivepulse.util.LocaleHelper.setLocale(getApplication(), language)
    }

    fun updateDefaultVehicle(vehicleId: String) {
        val updated = settings.copy(defaultVehicleId = vehicleId)
        settings = updated
        settingsManager.saveSettings(updated)
        vehicleRepository.setDefaultVehicle(vehicleId)
    }
}
