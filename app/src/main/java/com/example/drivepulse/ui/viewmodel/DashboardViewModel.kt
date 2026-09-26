package com.example.drivepulse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.drivepulse.data.model.FuelRecord
import com.example.drivepulse.data.model.MaintenanceRecord
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.data.repository.VehicleRepository
import com.example.drivepulse.util.HealthScoreResult
import java.util.UUID

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val vehicleRepository = VehicleRepository.getInstance(application)

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        val vehiclesList = vehicleRepository.getVehicles()
        val defaultVehicle = vehicleRepository.getDefaultVehicle()

        if (defaultVehicle != null) {
            val vId = defaultVehicle.id
            val health = vehicleRepository.getVehicleHealthScore(vId)
            val avgFuel = vehicleRepository.calculateAverageFuelConsumption(vId)
            val fuelSpend = vehicleRepository.getTotalFuelSpending(vId)
            val maintSpend = vehicleRepository.getTotalMaintenanceSpending(vId)

            uiState = uiState.copy(
                selectedVehicle = defaultVehicle,
                vehicles = vehiclesList,
                healthScore = health,
                avgFuelConsumption = avgFuel,
                totalFuelSpending = fuelSpend,
                totalMaintenanceSpending = maintSpend
            )
        } else {
            uiState = uiState.copy(vehicles = vehiclesList)
        }
    }

    fun selectVehicle(vehicle: Vehicle) {
        val vId = vehicle.id
        val health = vehicleRepository.getVehicleHealthScore(vId)
        val avgFuel = vehicleRepository.calculateAverageFuelConsumption(vId)
        val fuelSpend = vehicleRepository.getTotalFuelSpending(vId)
        val maintSpend = vehicleRepository.getTotalMaintenanceSpending(vId)

        uiState = uiState.copy(
            selectedVehicle = vehicle,
            healthScore = health,
            avgFuelConsumption = avgFuel,
            totalFuelSpending = fuelSpend,
            totalMaintenanceSpending = maintSpend
        )
    }

    fun openQuickAddFuel() {
        uiState = uiState.copy(isQuickAddFuelOpen = true)
    }

    fun closeQuickAddFuel() {
        uiState = uiState.copy(isQuickAddFuelOpen = false)
    }

    fun openQuickAddMaintenance() {
        uiState = uiState.copy(isQuickAddMaintenanceOpen = true)
    }

    fun closeQuickAddMaintenance() {
        uiState = uiState.copy(isQuickAddMaintenanceOpen = false)
    }

    fun quickAddFuel(odometer: Int, litres: Double, pricePerLitre: Double, date: String) {
        val vehicle = uiState.selectedVehicle ?: return
        val newRecord = FuelRecord(
            id = UUID.randomUUID().toString(),
            vehicleId = vehicle.id,
            date = date,
            odometer = odometer,
            litres = litres,
            pricePerLitre = pricePerLitre,
            totalCost = litres * pricePerLitre
        )
        vehicleRepository.addFuelRecord(newRecord)
        closeQuickAddFuel()
        loadDashboardData()
    }

    fun quickAddMaintenance(serviceType: String, cost: Double, odometer: Int, date: String, nextOdometer: Int) {
        val vehicle = uiState.selectedVehicle ?: return
        val newRecord = MaintenanceRecord(
            id = UUID.randomUUID().toString(),
            vehicleId = vehicle.id,
            serviceType = serviceType,
            date = date,
            odometer = odometer,
            cost = cost,
            nextServiceOdometer = nextOdometer
        )
        vehicleRepository.addMaintenanceRecord(newRecord)
        closeQuickAddMaintenance()
        loadDashboardData()
    }
}
