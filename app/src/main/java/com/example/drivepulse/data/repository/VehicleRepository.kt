package com.example.drivepulse.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.drivepulse.data.model.FuelRecord
import com.example.drivepulse.data.model.MaintenanceRecord
import com.example.drivepulse.data.model.Vehicle
import com.example.drivepulse.data.remote.api.ApiClient
import com.example.drivepulse.data.remote.dto.VehicleDto
import com.example.drivepulse.data.remote.firebase.FirestoreService
import com.example.drivepulse.util.HealthScoreCalculator
import com.example.drivepulse.util.HealthScoreResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Shared Persistent Repository providing vehicle data, fuel records,
 * maintenance logs, health score, local persistence, REST API integration, and Cloud Firestore sync.
 */
class VehicleRepository private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("drivepulse_data_store", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val api = ApiClient.api
    private val firestoreService = FirestoreService()

    companion object {
        @Volatile
        private var INSTANCE: VehicleRepository? = null

        fun getInstance(context: Context): VehicleRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: VehicleRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }

        private const val KEY_VEHICLES = "key_vehicles"
        private const val KEY_FUEL_RECORDS = "key_fuel_records"
        private const val KEY_MAINTENANCE_RECORDS = "key_maintenance_records"
    }

    private val vehicles = mutableListOf<Vehicle>()
    private val fuelRecords = mutableListOf<FuelRecord>()
    private val maintenanceRecords = mutableListOf<MaintenanceRecord>()

    init {
        loadFromPersistentStorage()
    }

    private fun loadFromPersistentStorage() {
        val vehiclesJson = prefs.getString(KEY_VEHICLES, null)
        if (!vehiclesJson.isNull_or_blank()) {
            try {
                val type = object : TypeToken<List<Vehicle>>() {}.type
                val savedVehicles: List<Vehicle> = gson.fromJson(vehiclesJson, type) ?: emptyList()
                if (savedVehicles.isNotEmpty()) {
                    vehicles.clear()
                    vehicles.addAll(savedVehicles)
                } else if (vehicles.isEmpty()) {
                    loadDefaultDemoData()
                }
            } catch (e: Exception) {
                if (vehicles.isEmpty()) loadDefaultDemoData()
            }
        } else if (vehicles.isEmpty()) {
            loadDefaultDemoData()
        }

        val fuelJson = prefs.getString(KEY_FUEL_RECORDS, null)
        if (!fuelJson.isNull_or_blank()) {
            try {
                val type = object : TypeToken<List<FuelRecord>>() {}.type
                val savedFuel: List<FuelRecord> = gson.fromJson(fuelJson, type) ?: emptyList()
                if (savedFuel.isNotEmpty()) {
                    fuelRecords.clear()
                    fuelRecords.addAll(savedFuel)
                } else if (fuelRecords.isEmpty()) {
                    loadDefaultDemoFuel()
                }
            } catch (e: Exception) {
                if (fuelRecords.isEmpty()) loadDefaultDemoFuel()
            }
        } else if (fuelRecords.isEmpty()) {
            loadDefaultDemoFuel()
        }

        val maintJson = prefs.getString(KEY_MAINTENANCE_RECORDS, null)
        if (!maintJson.isNull_or_blank()) {
            try {
                val type = object : TypeToken<List<MaintenanceRecord>>() {}.type
                val savedMaint: List<MaintenanceRecord> = gson.fromJson(maintJson, type) ?: emptyList()
                if (savedMaint.isNotEmpty()) {
                    maintenanceRecords.clear()
                    maintenanceRecords.addAll(savedMaint)
                } else if (maintenanceRecords.isEmpty()) {
                    loadDefaultDemoMaintenance()
                }
            } catch (e: Exception) {
                if (maintenanceRecords.isEmpty()) loadDefaultDemoMaintenance()
            }
        } else if (maintenanceRecords.isEmpty()) {
            loadDefaultDemoMaintenance()
        }
    }

    private fun CharSequence?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()

    private fun loadDefaultDemoData() {
        if (vehicles.isEmpty()) {
            vehicles.add(
                Vehicle(
                    id = "v1",
                    userId = "default_user",
                    make = "Toyota",
                    model = "Corolla 1.8 Exclusive",
                    year = 2022,
                    registrationNumber = "ND 123-456",
                    vin = "AHTFX349830219482",
                    fuelType = "Petrol",
                    currentOdometer = 48500,
                    serviceIntervalKm = 15000,
                    isDefault = true
                )
            )
            saveVehiclesToDisk()
        }
    }

    private fun loadDefaultDemoFuel() {
        if (fuelRecords.isEmpty()) {
            fuelRecords.addAll(
                listOf(
                    FuelRecord("f1", "v1", "2026-03-10", 47900, 45.0, 23.50, 1057.50, "Petrol", "Shell Ultra City"),
                    FuelRecord("f2", "v1", "2026-03-24", 48500, 46.5, 23.80, 1106.70, "Petrol", "Engen QuickShop")
                )
            )
            saveFuelToDisk()
        }
    }

    private fun loadDefaultDemoMaintenance() {
        if (maintenanceRecords.isEmpty()) {
            maintenanceRecords.add(
                MaintenanceRecord("m1", "v1", "Oil & Filter Change", "2026-01-15", 45000, 1450.0, "Synthetic engine oil replacement", 60000)
            )
            saveMaintenanceToDisk()
        }
    }

    private fun saveVehiclesToDisk() {
        prefs.edit().putString(KEY_VEHICLES, gson.toJson(vehicles)).commit()
    }

    private fun saveFuelToDisk() {
        prefs.edit().putString(KEY_FUEL_RECORDS, gson.toJson(fuelRecords)).commit()
    }

    private fun saveMaintenanceToDisk() {
        prefs.edit().putString(KEY_MAINTENANCE_RECORDS, gson.toJson(maintenanceRecords)).commit()
    }

    fun getVehicles(): List<Vehicle> = vehicles.toList()

    fun getVehicleById(vehicleId: String): Vehicle? = vehicles.find { it.id == vehicleId }

    fun getDefaultVehicle(): Vehicle? = vehicles.find { it.isDefault } ?: vehicles.firstOrNull()

    fun getFuelRecords(vehicleId: String): List<FuelRecord> =
        fuelRecords.filter { it.vehicleId == vehicleId }.sortedByDescending { it.odometer }

    fun getMaintenanceRecords(vehicleId: String): List<MaintenanceRecord> =
        maintenanceRecords.filter { it.vehicleId == vehicleId }.sortedByDescending { it.odometer }

    fun addVehicle(vehicle: Vehicle) {
        for (i in vehicles.indices) {
            vehicles[i] = vehicles[i].copy(isDefault = false)
        }
        val newDefaultVehicle = vehicle.copy(isDefault = true)
        vehicles.add(0, newDefaultVehicle)
        saveVehiclesToDisk()
    }

    fun updateVehicle(vehicle: Vehicle) {
        val index = vehicles.indexOfFirst { it.id == vehicle.id }
        if (index != -1) {
            vehicles[index] = vehicle
            if (vehicle.isDefault) {
                for (i in vehicles.indices) {
                    if (vehicles[i].id != vehicle.id) {
                        vehicles[i] = vehicles[i].copy(isDefault = false)
                    }
                }
            }
            saveVehiclesToDisk()
        }
    }

    fun deleteVehicle(vehicleId: String) {
        vehicles.removeAll { it.id == vehicleId }
        fuelRecords.removeAll { it.vehicleId == vehicleId }
        maintenanceRecords.removeAll { it.vehicleId == vehicleId }
        if (vehicles.none { it.isDefault } && vehicles.isNotEmpty()) {
            vehicles[0] = vehicles[0].copy(isDefault = true)
        }
        saveVehiclesToDisk()
        saveFuelToDisk()
        saveMaintenanceToDisk()
    }

    fun setDefaultVehicle(vehicleId: String) {
        for (i in vehicles.indices) {
            vehicles[i] = vehicles[i].copy(isDefault = (vehicles[i].id == vehicleId))
        }
        saveVehiclesToDisk()
    }

    fun addFuelRecord(record: FuelRecord) {
        fuelRecords.add(record)
        val vehicleIndex = vehicles.indexOfFirst { it.id == record.vehicleId }
        if (vehicleIndex != -1 && record.odometer > vehicles[vehicleIndex].currentOdometer) {
            vehicles[vehicleIndex] = vehicles[vehicleIndex].copy(currentOdometer = record.odometer)
            saveVehiclesToDisk()
        }
        saveFuelToDisk()
    }

    fun addMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceRecords.add(record)
        val vehicleIndex = vehicles.indexOfFirst { it.id == record.vehicleId }
        if (vehicleIndex != -1 && record.odometer > vehicles[vehicleIndex].currentOdometer) {
            vehicles[vehicleIndex] = vehicles[vehicleIndex].copy(currentOdometer = record.odometer)
            saveVehiclesToDisk()
        }
        saveMaintenanceToDisk()
    }

    fun calculateAverageFuelConsumption(vehicleId: String): Double {
        val sortedLogs = getFuelRecords(vehicleId).sortedBy { it.odometer }
        if (sortedLogs.size < 2) return 0.0

        val totalLitres = sortedLogs.drop(1).sumOf { it.litres }
        val distanceKm = sortedLogs.last().odometer - sortedLogs.first().odometer

        if (distanceKm <= 0) return 0.0
        return (totalLitres / distanceKm) * 100.0
    }

    fun getTotalFuelSpending(vehicleId: String): Double {
        return getFuelRecords(vehicleId).sumOf { it.totalCost }
    }

    fun getTotalMaintenanceSpending(vehicleId: String): Double {
        return getMaintenanceRecords(vehicleId).sumOf { it.cost }
    }

    fun getVehicleHealthScore(vehicleId: String): HealthScoreResult {
        val vehicle = vehicles.find { it.id == vehicleId }
        val logs = getMaintenanceRecords(vehicleId)
        return HealthScoreCalculator.calculateHealthScore(vehicle, logs)
    }

    // --- Cloud Firestore Methods ---

    suspend fun saveVehicleToFirestore(vehicle: Vehicle): Result<Unit> {
        val result = firestoreService.saveVehicle(vehicle)
        if (result.isSuccess) {
            addVehicle(vehicle)
        }
        return result
    }

    suspend fun fetchVehicleFromFirestoreById(vehicleId: String): Result<Vehicle?> {
        val result = firestoreService.getVehicleById(vehicleId)
        if (result.isSuccess) {
            result.getOrNull()?.let { fetchedVehicle ->
                val index = vehicles.indexOfFirst { it.id == fetchedVehicle.id }
                if (index == -1) {
                    vehicles.add(fetchedVehicle)
                } else {
                    vehicles[index] = fetchedVehicle
                }
                saveVehiclesToDisk()
            }
        }
        return result
    }

    suspend fun fetchAllVehiclesFromFirestore(): Result<List<Vehicle>> {
        val result = firestoreService.getAllVehicles()
        if (result.isSuccess) {
            val remoteVehicles = result.getOrDefault(emptyList())
            for (remote in remoteVehicles) {
                val index = vehicles.indexOfFirst { it.id == remote.id }
                if (index == -1) {
                    vehicles.add(remote)
                } else {
                    vehicles[index] = remote
                }
            }
            saveVehiclesToDisk()
        }
        return result
    }

    // --- REST API Methods ---

    suspend fun fetchVehiclesFromApi(): Result<List<Vehicle>> {
        return try {
            val response = api.getVehicles()
            if (response.isSuccessful) {
                val dtoList = response.body() ?: emptyList()
                val apiVehicles = dtoList.map { it.toVehicle() }
                if (apiVehicles.isNotEmpty()) {
                    for (apiVehicle in apiVehicles) {
                        val index = vehicles.indexOfFirst { it.id == apiVehicle.id }
                        if (index == -1) {
                            vehicles.add(apiVehicle)
                        } else {
                            vehicles[index] = apiVehicle
                        }
                    }
                    saveVehiclesToDisk()
                }
                Result.success(vehicles.toList())
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addVehicleToApi(vehicle: Vehicle): Result<Vehicle> {
        return try {
            val response = api.addVehicle(vehicle.toDtoForCreate())
            if (response.isSuccessful) {
                val body = response.body() ?: return Result.failure(Exception("Empty response"))
                val savedVehicle = body.toVehicle()
                addVehicle(savedVehicle)
                Result.success(savedVehicle)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVehicleOnApi(vehicle: Vehicle): Result<Vehicle> {
        return try {
            val serverId = vehicle.id.toLongOrNull() ?: return Result.failure(Exception("Invalid ID"))
            val response = api.updateVehicle(serverId.toString(), vehicle.toDto(serverId))
            if (response.isSuccessful) {
                val body = response.body() ?: return Result.failure(Exception("Empty response"))
                val updated = body.toVehicle()
                updateVehicle(updated)
                Result.success(updated)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVehicleFromApi(vehicleId: String): Result<Boolean> {
        return try {
            val serverId = vehicleId.toLongOrNull() ?: return Result.failure(Exception("Invalid ID"))
            val response = api.deleteVehicle(serverId.toString())
            if (response.isSuccessful) {
                deleteVehicle(vehicleId)
                Result.success(true)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun VehicleDto.toVehicle(): Vehicle = Vehicle(
        id = id.toString(),
        userId = "default_user",
        make = make,
        model = model,
        year = year,
        registrationNumber = registrationNumber ?: "",
        vin = "",
        fuelType = fuelType,
        currentOdometer = currentMileage,
        serviceIntervalKm = 15000,
        isDefault = false
    )

    private fun Vehicle.toDto(serverId: Long): VehicleDto = VehicleDto(
        id = serverId,
        make = make,
        model = model,
        year = year,
        registrationNumber = registrationNumber,
        currentMileage = currentOdometer,
        fuelType = fuelType
    )

    private fun Vehicle.toDtoForCreate(): VehicleDto = VehicleDto(
        id = 0L,
        make = make,
        model = model,
        year = year,
        registrationNumber = registrationNumber,
        currentMileage = currentOdometer,
        fuelType = fuelType
    )
}
