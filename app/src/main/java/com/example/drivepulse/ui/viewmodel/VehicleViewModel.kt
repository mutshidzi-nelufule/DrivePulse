package com.example.drivepulse.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.data.repository.VehicleRepository
import com.example.drivepulse.util.ValidationUtils
import com.example.drivepulse.util.VehicleValidationResult
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class VehicleFormState(
    val id: String? = null,
    val make: String = "",
    val model: String = "",
    val yearText: String = "",
    val regNumber: String = "",
    val vin: String = "",
    val fuelType: String = "Petrol",
    val odometerText: String = "",
    val serviceIntervalText: String = "15000",
    val isDefault: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class VehicleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VehicleRepository.getInstance(application)

    var vehicles by mutableStateOf<List<Vehicle>>(repository.getVehicles())
        private set

    var formState by mutableStateOf(VehicleFormState())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadVehicles()
    }

    fun refreshVehicles() {
        vehicles = repository.getVehicles()
        Log.d("DrivePulse", "VehicleViewModel refreshVehicles: count = ${vehicles.size}")
    }

    fun loadVehicles() {
        refreshVehicles()
        viewModelScope.launch {
            // Fetch from Cloud Firestore (e.g. document vehicle_001)
            repository.fetchAllVehiclesFromFirestore().onSuccess {
                refreshVehicles()
            }
            // Fetch from Spring Boot API in background
            repository.fetchVehiclesFromApi().onSuccess {
                refreshVehicles()
            }
        }
    }

    fun onMakeChange(value: String) {
        formState = formState.copy(make = value, errorMessage = null)
    }

    fun onModelChange(value: String) {
        formState = formState.copy(model = value, errorMessage = null)
    }

    fun onYearChange(value: String) {
        formState = formState.copy(yearText = value, errorMessage = null)
    }

    fun onRegNumberChange(value: String) {
        formState = formState.copy(regNumber = value, errorMessage = null)
    }

    fun onVinChange(value: String) {
        formState = formState.copy(vin = value, errorMessage = null)
    }

    fun onFuelTypeChange(value: String) {
        formState = formState.copy(fuelType = value)
    }

    fun onOdometerChange(value: String) {
        formState = formState.copy(odometerText = value, errorMessage = null)
    }

    fun onServiceIntervalChange(value: String) {
        formState = formState.copy(serviceIntervalText = value)
    }

    fun onIsDefaultChange(value: Boolean) {
        formState = formState.copy(isDefault = value)
    }

    fun prepareNewVehicleForm() {
        formState = VehicleFormState()
    }

    fun loadVehicleForEdit(vehicleId: String) {
        val vehicle = repository.getVehicleById(vehicleId) ?: return
        formState = VehicleFormState(
            id = vehicle.id,
            make = vehicle.make,
            model = vehicle.model,
            yearText = vehicle.year.toString(),
            regNumber = vehicle.registrationNumber,
            vin = vehicle.vin,
            fuelType = vehicle.fuelType,
            odometerText = vehicle.currentOdometer.toString(),
            serviceIntervalText = vehicle.serviceIntervalKm.toString(),
            isDefault = vehicle.isDefault
        )
    }

    fun saveVehicle(onSuccess: () -> Unit) {
        Log.d("DrivePulse", "saveVehicle clicked with form: make='${formState.make}', model='${formState.model}', year='${formState.yearText}'")

        val validation = ValidationUtils.validateVehicle(
            make = formState.make,
            model = formState.model,
            yearText = formState.yearText,
            registrationNumber = formState.regNumber,
            odometerText = formState.odometerText,
            vin = formState.vin
        )

        when (validation) {
            is VehicleValidationResult.Error -> {
                Log.e("DrivePulse", "Validation failed: ${validation.message}")
                formState = formState.copy(errorMessage = validation.message)
            }
            is VehicleValidationResult.Success -> {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val vehicleId = if (formState.id.isNullOrBlank()) UUID.randomUUID().toString() else formState.id!!

                val vehicle = Vehicle(
                    id = vehicleId,
                    userId = "default_user",
                    make = formState.make.trim(),
                    model = formState.model.trim(),
                    year = formState.yearText.toIntOrNull() ?: currentYear,
                    registrationNumber = formState.regNumber.trim().ifEmpty { "REG-NEW" },
                    vin = formState.vin.trim(),
                    fuelType = formState.fuelType.ifBlank { "Petrol" },
                    currentOdometer = formState.odometerText.toIntOrNull() ?: 0,
                    serviceIntervalKm = formState.serviceIntervalText.toIntOrNull() ?: 15000,
                    isDefault = formState.isDefault
                )

                // Save locally first for instant UI response
                if (formState.id == null) {
                    repository.addVehicle(vehicle)
                } else {
                    repository.updateVehicle(vehicle)
                }

                refreshVehicles()
                prepareNewVehicleForm()
                onSuccess()

                // Sync to Cloud Firestore and REST API in background
                viewModelScope.launch {
                    repository.saveVehicleToFirestore(vehicle)
                    if (formState.id == null) {
                        repository.addVehicleToApi(vehicle)
                    } else {
                        repository.updateVehicleOnApi(vehicle)
                    }
                }
            }
        }
    }


    fun deleteVehicle(vehicleId: String, onSuccess: () -> Unit = {}) {
        repository.deleteVehicle(vehicleId)
        refreshVehicles()
        onSuccess()

        viewModelScope.launch {
            repository.deleteVehicleFromApi(vehicleId)
        }
    }

    fun setDefaultVehicle(vehicleId: String) {
        repository.setDefaultVehicle(vehicleId)
        refreshVehicles()
    }

    fun clearError() {
        errorMessage = null
        formState = formState.copy(errorMessage = null)
    }
}
