package com.example.drivepulse.data.local

import android.content.Context
import android.content.SharedPreferences

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class DistanceUnit { KM, MILES }
enum class FuelUnit { LITRES, GALLONS }
enum class Currency { ZAR, USD, EUR, GBP }

data class SettingsPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val fuelUnit: FuelUnit = FuelUnit.LITRES,
    val currency: Currency = Currency.ZAR,
    val notificationsEnabled: Boolean = true,
    val language: String = "English",
    val defaultVehicleId: String? = "v1"
) {
    fun getCurrencySymbol(): String = when (currency) {
        Currency.ZAR -> "R"
        Currency.USD -> "$"
        Currency.EUR -> "€"
        Currency.GBP -> "£"
    }

    fun getDistanceUnitLabel(): String = when (distanceUnit) {
        DistanceUnit.KM -> "km"
        DistanceUnit.MILES -> "mi"
    }

    fun getFuelUnitLabel(): String = when (fuelUnit) {
        FuelUnit.LITRES -> "L"
        FuelUnit.GALLONS -> "gal"
    }
}

/**
 * Persists application settings and preferences across sessions.
 */
class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("drivepulse_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "key_theme"
        private const val KEY_DISTANCE_UNIT = "key_distance_unit"
        private const val KEY_FUEL_UNIT = "key_fuel_unit"
        private const val KEY_CURRENCY = "key_currency"
        private const val KEY_NOTIFICATIONS = "key_notifications"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_DEFAULT_VEHICLE = "key_default_vehicle"
    }

    fun getSettings(): SettingsPreferences {
        val theme = ThemeMode.valueOf(prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
        val distance = DistanceUnit.valueOf(prefs.getString(KEY_DISTANCE_UNIT, DistanceUnit.KM.name) ?: DistanceUnit.KM.name)
        val fuel = FuelUnit.valueOf(prefs.getString(KEY_FUEL_UNIT, FuelUnit.LITRES.name) ?: FuelUnit.LITRES.name)
        val curr = Currency.valueOf(prefs.getString(KEY_CURRENCY, Currency.ZAR.name) ?: Currency.ZAR.name)
        val notifs = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        val lang = prefs.getString(KEY_LANGUAGE, "English") ?: "English"
        val vehicleId = prefs.getString(KEY_DEFAULT_VEHICLE, "v1")

        return SettingsPreferences(
            themeMode = theme,
            distanceUnit = distance,
            fuelUnit = fuel,
            currency = curr,
            notificationsEnabled = notifs,
            language = lang,
            defaultVehicleId = vehicleId
        )
    }

    fun saveSettings(settings: SettingsPreferences) {
        prefs.edit().apply {
            putString(KEY_THEME, settings.themeMode.name)
            putString(KEY_DISTANCE_UNIT, settings.distanceUnit.name)
            putString(KEY_FUEL_UNIT, settings.fuelUnit.name)
            putString(KEY_CURRENCY, settings.currency.name)
            putBoolean(KEY_NOTIFICATIONS, settings.notificationsEnabled)
            putString(KEY_LANGUAGE, settings.language)
            putString(KEY_DEFAULT_VEHICLE, settings.defaultVehicleId)
            apply()
        }
    }
}
