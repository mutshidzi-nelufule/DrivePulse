package com.example.drivepulse.data.repository

import android.content.Context
import com.example.drivepulse.data.local.SettingsManager
import com.example.drivepulse.data.local.SettingsPreferences

class SettingsRepository(context: Context) {

    private val manager = SettingsManager(context)

    fun getSettings(): SettingsPreferences = manager.getSettings()

    fun saveSettings(settings: SettingsPreferences) = manager.saveSettings(settings)
}
