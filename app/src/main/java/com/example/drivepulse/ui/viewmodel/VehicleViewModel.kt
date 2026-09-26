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

    var vehicles by mutableStateOf<List<Vehicle>>(emptyList())
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

        Log.d(
            "DrivePulse",
            "VehicleViewModel refreshVehicles: count = ${vehicles.size}"
        )
    }

    fun loadVehicles() {

        isLoading = true

        viewModelScope.launch {

            val result = repository.fetchVehiclesFromApi()

            result.onSuccess { apiVehicles ->

                vehicles = apiVehicles

                Log.d(
                    "DrivePulse",
                    "Vehicles loaded from API: ${apiVehicles.size}"
                )

            }.onFailure { error ->

                Log.e(
                    "DrivePulse",
                    "Failed to load vehicles from API",
                    error
                )

                // Keep locally stored vehicles if API is unavailable
                vehicles = repository.getVehicles()
            }

            isLoading = false
        }
    }

    fun onMakeChange(value: String) {
        formState = formState.copy(
            make = value,
            errorMessage = null
        )
    }

    fun onModelChange(value: String) {
        formState = formState.copy(
            model = value,
            errorMessage = null
        )
    }

    fun onYearChange(value: String) {
        formState = formState.copy(
            yearText = value,
            errorMessage = null
        )
    }

    fun onRegNumberChange(value: String) {
        formState = formState.copy(
            regNumber = value,
            errorMessage = null
        )
    }

    fun onVinChange(value: String) {
        formState = formState.copy(
            vin = value,
            errorMessage = null
        )
    }

    fun onFuelTypeChange(value: String) {
        formState = formState.copy(
            fuelType = value
        )
    }

    fun onOdometerChange(value: String) {
        formState = formState.copy(
            odometerText = value,
            errorMessage = null
        )
    }

    fun onServiceIntervalChange(value: String) {
        formState = formState.copy(
            serviceIntervalText = value
        )
    }

    fun onIsDefaultChange(value: Boolean) {
        formState = formState.copy(
            isDefault = value
        )
    }

    fun prepareNewVehicleForm() {
        formState = VehicleFormState()
    }

    fun loadVehicleForEdit(vehicleId: String) {

        val vehicle = repository.getVehicleById(vehicleId)
            ?: return

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

        Log.d(
            "DrivePulse",
            "saveVehicle clicked: make=${formState.make}, model=${formState.model}"
        )

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

                Log.e(
                    "DrivePulse",
                    "Validation failed: ${validation.message}"
                )

                formState = formState.copy(
                    errorMessage = validation.message
                )
            }

            is VehicleValidationResult.Success -> {

                val currentId = formState.id

                val currentYear =
                    Calendar.getInstance().get(Calendar.YEAR)

                val yearInt =
                    formState.yearText.toIntOrNull()
                        ?: currentYear

                val odoInt =
                    formState.odometerText.toIntOrNull()
                        ?: 0

                val regStr =
                    formState.regNumber.trim()
                        .ifEmpty { "REG-NEW" }

                val vehicle = Vehicle(
                    id = currentId ?: "",
                    userId = "default_user",
                    make = formState.make.trim(),
                    model = formState.model.trim(),
                    year = yearInt,
                    registrationNumber = regStr,
                    vin = formState.vin.trim(),
                    fuelType = formState.fuelType.ifBlank {
                        "Petrol"
                    },
                    currentOdometer = odoInt,
                    serviceIntervalKm =
                        formState.serviceIntervalText
                            .toIntOrNull()
                            ?: 15000,
                    isDefault = formState.isDefault
                )

                isLoading = true

                viewModelScope.launch {

                    if (currentId == null) {

                        // NEW VEHICLE
                        Log.d(
                            "DrivePulse",
                            "Sending new vehicle to API..."
                        )

                        val result =
                            repository.addVehicleToApi(vehicle)

                        result.onSuccess { savedVehicle ->

                            Log.d(
                                "DrivePulse",
                                "Vehicle successfully saved to API. ID=${savedVehicle.id}"
                            )

                            vehicles =
                                repository.getVehicles()

                            formState =
                                VehicleFormState()

                            isLoading = false

                            onSuccess()

                        }.onFailure { error ->

                            Log.e(
                                "DrivePulse",
                                "API vehicle creation failed",
                                error
                            )

                            formState =
                                formState.copy(
                                    errorMessage =
                                        "Could not save vehicle to server: ${error.message}"
                                )

                            isLoading = false
                        }

                    } else {

                        // UPDATE VEHICLE
                        Log.d(
                            "DrivePulse",
                            "Updating vehicle ID=$currentId"
                        )

                        val result =
                            repository.updateVehicleOnApi(vehicle)

                        result.onSuccess { updatedVehicle ->

                            Log.d(
                                "DrivePulse",
                                "Vehicle successfully updated. ID=${updatedVehicle.id}"
                            )

                            vehicles =
                                repository.getVehicles()

                            formState =
                                VehicleFormState()

                            isLoading = false

                            onSuccess()

                        }.onFailure { error ->

                            Log.e(
                                "DrivePulse",
                                "API vehicle update failed",
                                error
                            )

                            formState =
                                formState.copy(
                                    errorMessage =
                                        "Could not update vehicle: ${error.message}"
                                )

                            isLoading = false
                        }
                    }
                }
            }
        }
    }

    fun deleteVehicle(
        vehicleId: String,
        onSuccess: () -> Unit = {}
    ) {

        isLoading = true

        viewModelScope.launch {

            val result =
                repository.deleteVehicleFromApi(vehicleId)

            result.onSuccess {

                Log.d(
                    "DrivePulse",
                    "Vehicle deleted from API: $vehicleId"
                )

                vehicles =
                    repository.getVehicles()

                isLoading = false

                onSuccess()

            }.onFailure { error ->

                Log.e(
                    "DrivePulse",
                    "API vehicle deletion failed",
                    error
                )

                errorMessage =
                    "Could not delete vehicle: ${error.message}"

                isLoading = false
            }
        }
    }

    fun setDefaultVehicle(vehicleId: String) {

        repository.setDefaultVehicle(vehicleId)

        refreshVehicles()
    }

    fun clearError() {

        errorMessage = null

        formState =
            formState.copy(
                errorMessage = null
            )
    }
}