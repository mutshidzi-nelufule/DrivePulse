package com.example.drivepulse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.drivepulse.data.model.FuelRecord
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.data.repository.VehicleRepository
import com.example.drivepulse.util.FuelCalculator
import com.example.drivepulse.util.FuelValidationResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class FuelFormState(
    val odometerText: String = "",
    val litresText: String = "",
    val pricePerLitreText: String = "23.80",
    val fuelStation: String = "",
    val isFullTank: Boolean = true,
    val notes: String = "",
    val errorMessage: String? = null
)

class FuelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VehicleRepository.getInstance(application)

    var selectedVehicle by mutableStateOf<Vehicle?>(repository.getDefaultVehicle())
        private set

    var fuelLogs by mutableStateOf<List<FuelRecord>>(emptyList())
        private set

    var formState by mutableStateOf(FuelFormState())
        private set

    init {
        loadFuelData()
    }

    fun loadFuelData() {
        val vehicle = repository.getDefaultVehicle() ?: repository.getVehicles().firstOrNull()
        selectedVehicle = vehicle
        if (vehicle != null) {
            fuelLogs = repository.getFuelRecords(vehicle.id)
            formState = formState.copy(odometerText = vehicle.currentOdometer.toString())
        } else {
            fuelLogs = emptyList()
        }
    }

    fun onOdometerChange(value: String) { formState = formState.copy(odometerText = value, errorMessage = null) }
    fun onLitresChange(value: String) { formState = formState.copy(litresText = value, errorMessage = null) }
    fun onPriceChange(value: String) { formState = formState.copy(pricePerLitreText = value, errorMessage = null) }
    fun onStationChange(value: String) { formState = formState.copy(fuelStation = value) }
    fun onIsFullTankChange(value: Boolean) { formState = formState.copy(isFullTank = value) }
    fun onNotesChange(value: String) { formState = formState.copy(notes = value) }

    fun calculateCurrentTotalCost(): Double {
        val litres = formState.litresText.toDoubleOrNull() ?: 0.0
        val price = formState.pricePerLitreText.toDoubleOrNull() ?: 0.0
        return FuelCalculator.calculateTotalCost(litres, price)
    }

    fun saveFuelLog(onSuccess: () -> Unit) {
        val vehicle = selectedVehicle ?: repository.getDefaultVehicle() ?: repository.getVehicles().firstOrNull()
        if (vehicle == null) {
            formState = formState.copy(errorMessage = "Please add a vehicle first in Vehicles screen.")
            return
        }

        val previousOdo = fuelLogs.maxOfOrNull { it.odometer } ?: vehicle.currentOdometer

        val validation = FuelCalculator.validateFuelInput(
            odometerText = formState.odometerText,
            litresText = formState.litresText,
            pricePerLitreText = formState.pricePerLitreText,
            previousOdometer = previousOdo
        )

        when (validation) {
            is FuelValidationResult.Error -> {
                formState = formState.copy(errorMessage = validation.message)
            }
            is FuelValidationResult.Success -> {
                val litres = formState.litresText.toDoubleOrNull() ?: 0.0
                val price = formState.pricePerLitreText.toDoubleOrNull() ?: 0.0
                val odo = formState.odometerText.toIntOrNull() ?: 0
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val newLog = FuelRecord(
                    id = UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    date = currentDate,
                    odometer = odo,
                    litres = litres,
                    pricePerLitre = price,
                    totalCost = litres * price,
                    fuelStation = formState.fuelStation.trim(),
                    isFullTank = formState.isFullTank,
                    notes = formState.notes.trim()
                )

                repository.addFuelRecord(newLog)
                loadFuelData()
                formState = FuelFormState(odometerText = newLog.odometer.toString())
                onSuccess()
            }
        }
    }

    fun calculateEfficiencyForLog(currentLog: FuelRecord): Double? {
        val sortedLogs = fuelLogs.sortedBy { it.odometer }
        val currentIndex = sortedLogs.indexOfFirst { it.id == currentLog.id }
        if (currentIndex <= 0) return null
        val prevLog = sortedLogs[currentIndex - 1]
        return FuelCalculator.calculateEfficiency(currentLog, prevLog)
    }

    fun getAverageConsumption(): Double {
        return FuelCalculator.calculateAverageConsumption(fuelLogs)
    }

    fun getTotalFuelSpent(): Double {
        return fuelLogs.sumOf { it.totalCost }
    }

    fun getTotalLitresFilled(): Double {
        return fuelLogs.sumOf { it.litres }
    }
}
