package com.example.drivepulse.ui.navigation

import androidx.compose.runtime.Composable
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

object NavRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val VEHICLES = "vehicles"
    const val ADD_VEHICLE = "add_vehicle"
    const val EDIT_VEHICLE = "edit_vehicle"
    const val VEHICLE_DETAILS = "vehicle_details"
    const val FUEL = "fuel"
    const val ADD_FUEL = "add_fuel"
    const val MAINTENANCE = "maintenance"
    const val ADD_MAINTENANCE = "add_maintenance"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
}

@Composable
fun DrivePulseNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val vehicleViewModel: VehicleViewModel = viewModel()
    val fuelViewModel: FuelViewModel = viewModel()
    val maintenanceViewModel: MaintenanceViewModel = viewModel()
    val reportViewModel: ReportsViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                isLoggedIn = authViewModel.isLoggedIn(),
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(NavRoutes.DASHBOARD) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.DASHBOARD) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.DASHBOARD) {
                        popUpTo(NavRoutes.REGISTER) { inclusive = true }
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.DASHBOARD) {
            val dashboardViewModel: DashboardViewModel = viewModel()
            DashboardScreen(
                authViewModel = authViewModel,
                dashboardViewModel = dashboardViewModel,
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.VEHICLES) {
            VehiclesScreen(
                vehicleViewModel = vehicleViewModel,
                onNavigateToAddVehicle = {
                    navController.navigate(NavRoutes.ADD_VEHICLE)
                },
                onNavigateToEditVehicle = { vId ->
                    vehicleViewModel.loadVehicleForEdit(vId)
                    navController.navigate("${NavRoutes.EDIT_VEHICLE}/$vId")
                },
                onNavigateToVehicleDetails = { vId ->
                    navController.navigate("${NavRoutes.VEHICLE_DETAILS}/$vId")
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.ADD_VEHICLE) {
            AddEditVehicleScreen(
                vehicleViewModel = vehicleViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${NavRoutes.EDIT_VEHICLE}/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            vehicleViewModel.loadVehicleForEdit(vId)
            AddEditVehicleScreen(
                vehicleViewModel = vehicleViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${NavRoutes.VEHICLE_DETAILS}/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailsScreen(
                vehicleId = vId,
                vehicleViewModel = vehicleViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId ->
                    vehicleViewModel.loadVehicleForEdit(editId)
                    navController.navigate("${NavRoutes.EDIT_VEHICLE}/$editId")
                }
            )
        }

        composable(NavRoutes.FUEL) {
            FuelHistoryScreen(
                fuelViewModel = fuelViewModel,
                onNavigateToAddFuel = {
                    navController.navigate(NavRoutes.ADD_FUEL)
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.ADD_FUEL) {
            AddFuelScreen(
                fuelViewModel = fuelViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MAINTENANCE) {
            MaintenanceHistoryScreen(
                viewModel = maintenanceViewModel,
                onNavigateToAddMaintenance = {
                    navController.navigate(NavRoutes.ADD_MAINTENANCE)
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.ADD_MAINTENANCE) {
            AddMaintenanceScreen(
                viewModel = maintenanceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.REPORTS) {
            reportViewModel.loadReportData()
            ReportsScreen(
                reportViewModel = reportViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.SETTINGS) {
            settingsViewModel.loadSettingsData()
            SettingsScreen(
                authViewModel = authViewModel,
                settingsViewModel = settingsViewModel,
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SETTINGS) { inclusive = true }
                    }
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}
