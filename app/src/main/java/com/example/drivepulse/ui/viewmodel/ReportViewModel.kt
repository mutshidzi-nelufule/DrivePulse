package com.example.drivepulse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.data.repository.VehicleRepository
import com.example.drivepulse.util.ReportCalculator
import com.example.drivepulse.util.ReportSummary

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VehicleRepository.getInstance(application)

    var selectedVehicle by mutableStateOf<Vehicle?>(repository.getDefaultVehicle())
        private set

    var reportSummary by mutableStateOf<ReportSummary?>(null)
        private set

    init {
        loadReportData()
    }

    fun loadReportData() {
        val vehicle = repository.getDefaultVehicle()
        selectedVehicle = vehicle
        if (vehicle != null) {
            val fuelLogs = repository.getFuelRecords(vehicle.id)
            val maintLogs = repository.getMaintenanceRecords(vehicle.id)
            reportSummary = ReportCalculator.generateReportSummary(vehicle, fuelLogs, maintLogs)
        }
    }
}
