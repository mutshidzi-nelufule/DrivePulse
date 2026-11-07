package com.example.drivepulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.drivepulse.ui.navigation.DrivePulseNavigation
import com.example.drivepulse.ui.theme.DrivePulseTheme
import com.example.drivepulse.ui.viewmodel.AuthViewModel
import com.example.drivepulse.ui.viewmodel.SettingsViewModel
import com.example.drivepulse.util.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val language = settingsViewModel.settings.language
            val context = LocalContext.current

            val localeContext = remember(language) {
                LocaleHelper.setLocale(context, language)
            }

            CompositionLocalProvider(LocalContext provides localeContext) {
                DrivePulseTheme {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    DrivePulseNavigation(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}
