package com.example.drivepulse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.drivepulse.ui.screens.AddEditVehicleScreen
import com.example.drivepulse.ui.screens.AddFuelScreen
import com.example.drivepulse.ui.screens.AddMaintenanceScreen
import com.example.drivepulse.ui.screens.DashboardScreen
import com.example.drivepulse.ui.screens.FuelHistoryScreen
import com.example.drivepulse.ui.screens.LoginScreen
import com.example.drivepulse.ui.screens.MaintenanceHistoryScreen
import com.example.drivepulse.ui.screens.RegisterScreen
import com.example.drivepulse.ui.screens.ReportsScreen
import com.example.drivepulse.ui.screens.SettingsScreen
import com.example.drivepulse.ui.screens.SplashScreen
import com.example.drivepulse.ui.screens.VehicleDetailsScreen
import com.example.drivepulse.ui.screens.VehiclesScreen
import com.example.drivepulse.ui.viewmodel.AuthViewModel
import com.example.drivepulse.ui.viewmodel.DashboardViewModel
import com.example.drivepulse.ui.viewmodel.FuelViewModel
import com.example.drivepulse.ui.viewmodel.MaintenanceViewModel
import com.example.drivepulse.ui.viewmodel.ReportsViewModel
import com.example.drivepulse.ui.viewmodel.SettingsViewModel
import com.example.drivepulse.ui.viewmodel.VehicleViewModel

@Composable
fun DrivePulseNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val vehicleViewModel: VehicleViewModel = viewModel()
    val fuelViewModel: FuelViewModel = viewModel()
    val maintenanceViewModel: MaintenanceViewModel = viewModel()
    val reportsViewModel: ReportsViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isLoggedIn = authViewModel.isLoggedIn(),
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel()
            LaunchedEffect(Unit) {
                dashboardViewModel.loadDashboardData()
            }
            DashboardScreen(
                authViewModel = authViewModel,
                dashboardViewModel = dashboardViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Vehicles.route) {
            LaunchedEffect(Unit) {
                vehicleViewModel.loadVehicles()
            }
            VehiclesScreen(
                vehicleViewModel = vehicleViewModel,
                onNavigateToAddVehicle = {
                    navController.navigate(Screen.AddVehicle.route)
                },
                onNavigateToEditVehicle = { vId ->
                    vehicleViewModel.loadVehicleForEdit(vId)
                    navController.navigate(Screen.EditVehicle.createRoute(vId))
                },
                onNavigateToVehicleDetails = { vId ->
                    navController.navigate(Screen.VehicleDetails.createRoute(vId))
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.AddVehicle.route) {
            AddEditVehicleScreen(
                vehicleViewModel = vehicleViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditVehicle.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            LaunchedEffect(vId) {
                vehicleViewModel.loadVehicleForEdit(vId)
            }
            AddEditVehicleScreen(
                vehicleViewModel = vehicleViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VehicleDetails.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailsScreen(
                vehicleId = vId,
                vehicleViewModel = vehicleViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId ->
                    vehicleViewModel.loadVehicleForEdit(editId)
                    navController.navigate(Screen.EditVehicle.createRoute(editId))
                }
            )
        }

        composable(Screen.Fuel.route) {
            LaunchedEffect(Unit) {
                fuelViewModel.loadFuelData()
            }
            FuelHistoryScreen(
                fuelViewModel = fuelViewModel,
                onNavigateToAddFuel = {
                    navController.navigate(Screen.AddFuel.route)
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.AddFuel.route) {
            AddFuelScreen(
                fuelViewModel = fuelViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Maintenance.route) {
            LaunchedEffect(Unit) {
                maintenanceViewModel.loadMaintenanceData()
            }
            MaintenanceHistoryScreen(
                viewModel = maintenanceViewModel,
                onNavigateToAddMaintenance = {
                    navController.navigate(Screen.AddMaintenance.route)
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.AddMaintenance.route) {
            AddMaintenanceScreen(
                viewModel = maintenanceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reports.route) {
            LaunchedEffect(Unit) {
                reportsViewModel.loadReportData()
            }
            ReportsScreen(
                reportViewModel = reportsViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Settings.route) {
            LaunchedEffect(Unit) {
                settingsViewModel.loadSettingsData()
            }
            SettingsScreen(
                authViewModel = authViewModel,
                settingsViewModel = settingsViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}
