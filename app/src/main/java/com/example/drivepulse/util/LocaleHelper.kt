package com.example.drivepulse.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val languageCode = when (language.lowercase()) {
            "tshivenda", "ve" -> "ve"
            else -> "en"
        }

        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
