package com.example.drivepulse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.drivepulse.data.model.MaintenanceRecord
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.data.repository.VehicleRepository
import com.example.drivepulse.util.MaintenanceValidationResult
import com.example.drivepulse.util.MaintenanceValidator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MaintenanceFormState(
    val serviceType: String = "Oil Change",
    val costText: String = "",
    val odometerText: String = "",
    val nextOdometerText: String = "",
    val description: String = "",
    val errorMessage: String? = null
)

class MaintenanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VehicleRepository.getInstance(application)

    var selectedVehicle by mutableStateOf<Vehicle?>(repository.getDefaultVehicle())
        private set

    var maintenanceLogs by mutableStateOf<List<MaintenanceRecord>>(emptyList())
        private set

    var formState by mutableStateOf(MaintenanceFormState())
        private set

    init {
        loadMaintenanceData()
    }

    fun loadMaintenanceData() {
        val vehicle = repository.getDefaultVehicle() ?: repository.getVehicles().firstOrNull()
        selectedVehicle = vehicle
        if (vehicle != null) {
            maintenanceLogs = repository.getMaintenanceRecords(vehicle.id)
            formState = formState.copy(
                odometerText = vehicle.currentOdometer.toString(),
                nextOdometerText = (vehicle.currentOdometer + vehicle.serviceIntervalKm).toString()
            )
        } else {
            maintenanceLogs = emptyList()
        }
    }

    fun onServiceTypeChange(value: String) { formState = formState.copy(serviceType = value, errorMessage = null) }
    fun onCostChange(value: String) { formState = formState.copy(costText = value, errorMessage = null) }
    fun onOdometerChange(value: String) {
        val odo = value.toIntOrNull() ?: 0
        val vInterval = selectedVehicle?.serviceIntervalKm ?: 15000
        formState = formState.copy(
            odometerText = value,
            nextOdometerText = if (odo > 0) (odo + vInterval).toString() else formState.nextOdometerText,
            errorMessage = null
        )
    }
    fun onNextOdometerChange(value: String) { formState = formState.copy(nextOdometerText = value, errorMessage = null) }
    fun onDescriptionChange(value: String) { formState = formState.copy(description = value) }

    fun saveMaintenanceRecord(onSuccess: () -> Unit) {
        val vehicle = selectedVehicle ?: repository.getDefaultVehicle() ?: repository.getVehicles().firstOrNull()
        if (vehicle == null) {
            formState = formState.copy(errorMessage = "Please add a vehicle first in Vehicles screen.")
            return
        }

        val validation = MaintenanceValidator.validateMaintenance(
            serviceType = formState.serviceType,
            costText = formState.costText,
            odometerText = formState.odometerText,
            nextOdometerText = formState.nextOdometerText
        )

        when (validation) {
            is MaintenanceValidationResult.Error -> {
                formState = formState.copy(errorMessage = validation.message)
            }
            is MaintenanceValidationResult.Success -> {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val newRecord = MaintenanceRecord(
                    id = UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    serviceType = formState.serviceType.trim(),
                    date = currentDate,
                    odometer = formState.odometerText.toIntOrNull() ?: 0,
                    cost = formState.costText.toDoubleOrNull() ?: 0.0,
                    description = formState.description.trim(),
                    nextServiceOdometer = formState.nextOdometerText.toIntOrNull() ?: 0
                )

                repository.addMaintenanceRecord(newRecord)
                loadMaintenanceData()
                formState = MaintenanceFormState(
                    odometerText = vehicle.currentOdometer.toString(),
                    nextOdometerText = (vehicle.currentOdometer + vehicle.serviceIntervalKm).toString()
                )
                onSuccess()
            }
        }
    }

    fun getTotalMaintenanceCost(): Double {
        return maintenanceLogs.sumOf { it.cost }
    }

    fun getUpcomingServices(): List<MaintenanceRecord> {
        val currentOdo = selectedVehicle?.currentOdometer ?: 0
        return maintenanceLogs.filter { it.nextServiceOdometer > 0 && it.nextServiceOdometer >= currentOdo }
    }
}
