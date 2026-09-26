package com.example.drivepulse.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Vehicles : Screen("vehicles")
    object AddVehicle : Screen("add_vehicle")
    object EditVehicle : Screen("edit_vehicle/{vehicleId}") {
        fun createRoute(vehicleId: String) = "edit_vehicle/$vehicleId"
    }
    object VehicleDetails : Screen("vehicle_details/{vehicleId}") {
        fun createRoute(vehicleId: String) = "vehicle_details/$vehicleId"
    }
    object Fuel : Screen("fuel")
    object AddFuel : Screen("add_fuel")
    object Maintenance : Screen("maintenance")
    object AddMaintenance : Screen("add_maintenance")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
