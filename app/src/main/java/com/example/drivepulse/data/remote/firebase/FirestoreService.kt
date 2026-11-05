package com.example.drivepulse.data.remote.firebase

import android.util.Log
import com.example.drivepulse.data.model.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Service managing Firebase Cloud Firestore database operations for DrivePulse.
 */
class FirestoreService {

    private val db = FirebaseFirestore.getInstance()
    private val vehiclesCollection = db.collection("vehicles")

    /**
     * Saves or updates a vehicle record in Cloud Firestore "vehicles" collection.
     */
    suspend fun saveVehicle(vehicle: Vehicle): Result<Unit> {
        return try {
            val docRef = if (vehicle.id.isBlank()) {
                vehiclesCollection.document()
            } else {
                vehiclesCollection.document(vehicle.id)
            }

            val vehicleData = hashMapOf(
                "make" to vehicle.make,
                "model" to vehicle.model,
                "year" to vehicle.year,
                "registrationNumber" to vehicle.registrationNumber,
                "fuelType" to vehicle.fuelType,
                "currentMileage" to vehicle.currentOdometer
            )

            docRef.set(vehicleData).await()
            Log.d("DrivePulseFirestore", "Saved vehicle to Firestore: ${docRef.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Error saving vehicle to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a single vehicle document by document ID (e.g. "vehicle_001").
     */
    suspend fun getVehicleById(vehicleId: String): Result<Vehicle?> {
        return try {
            val doc = vehiclesCollection.document(vehicleId).get().await()
            if (doc.exists()) {
                val vehicle = Vehicle(
                    id = doc.id,
                    userId = "default_user",
                    make = doc.getString("make") ?: "",
                    model = doc.getString("model") ?: "",
                    year = doc.getLong("year")?.toInt() ?: doc.getString("year")?.toIntOrNull() ?: 2020,
                    registrationNumber = doc.getString("registrationNumber") ?: "",
                    fuelType = doc.getString("fuelType") ?: "Petrol",
                    currentOdometer = doc.getLong("currentMileage")?.toInt()
                        ?: doc.getLong("currentOdometer")?.toInt()
                        ?: 0,
                    isDefault = false
                )
                Log.d("DrivePulseFirestore", "Fetched vehicle by ID ($vehicleId): ${vehicle.getDisplayName()}")
                Result.success(vehicle)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Error fetching vehicle by ID ($vehicleId)", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves all vehicle documents from the Cloud Firestore "vehicles" collection.
     */
    suspend fun getAllVehicles(): Result<List<Vehicle>> {
        return try {
            val snapshot = vehiclesCollection.get().await()
            val list = snapshot.documents.map { doc ->
                Vehicle(
                    id = doc.id,
                    userId = "default_user",
                    make = doc.getString("make") ?: "",
                    model = doc.getString("model") ?: "",
                    year = doc.getLong("year")?.toInt() ?: doc.getString("year")?.toIntOrNull() ?: 2020,
                    registrationNumber = doc.getString("registrationNumber") ?: "",
                    fuelType = doc.getString("fuelType") ?: "Petrol",
                    currentOdometer = doc.getLong("currentMileage")?.toInt()
                        ?: doc.getLong("currentOdometer")?.toInt()
                        ?: 0,
                    isDefault = false
                )
            }
            Log.d("DrivePulseFirestore", "Fetched ${list.size} vehicles from Cloud Firestore")
            Result.success(list)
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Error fetching vehicles from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a vehicle document from Cloud Firestore.
     */
    suspend fun deleteVehicle(vehicleId: String): Result<Unit> {
        return try {
            vehiclesCollection.document(vehicleId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
