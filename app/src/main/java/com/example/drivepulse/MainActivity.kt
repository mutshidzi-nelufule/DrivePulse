package com.example.drivepulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.drivepulse.ui.navigation.DrivePulseNavigation
import com.example.drivepulse.ui.theme.DrivePulseTheme
import com.example.drivepulse.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
