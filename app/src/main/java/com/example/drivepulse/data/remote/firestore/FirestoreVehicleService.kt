package com.example.drivepulse.data.remote.firestore

import android.util.Log
import com.example.drivepulse.data.model.Vehicle
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Isolated Cloud Firestore service for DrivePulse vehicle records.
 * Interacts with the "vehicles" collection in Firebase Cloud Firestore.
 */
class FirestoreVehicleService {

    private val db = FirebaseFirestore.getInstance()
    private val vehiclesCollection = db.collection("vehicles")

    /**
     * Saves or updates a vehicle record in Cloud Firestore "vehicles" collection.
     */
    suspend fun saveVehicle(vehicle: Vehicle): Result<String> {
        return try {
            val docRef = if (vehicle.id.isBlank()) {
                vehiclesCollection.document()
            } else {
                vehiclesCollection.document(vehicle.id)
            }

            val vehicleData: Map<String, Any> = mapOf(
                "make" to vehicle.make,
                "model" to vehicle.model,
                "year" to vehicle.year,
                "registrationNumber" to vehicle.registrationNumber,
                "fuelType" to vehicle.fuelType,
                "currentMileage" to vehicle.currentOdometer
            )

            docRef.set(vehicleData).await()
            Log.d("DrivePulseFirestore", "Saved vehicle to Firestore with doc ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Error saving vehicle to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a single vehicle document by ID (e.g. "vehicle_001").
     */
    suspend fun getVehicleById(vehicleId: String): Result<Vehicle?> {
        return try {
            val doc: DocumentSnapshot = vehiclesCollection.document(vehicleId).get().await()
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
                Result.success(vehicle)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Error fetching vehicle $vehicleId from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves all vehicle documents from Cloud Firestore and converts them to Vehicle models.
     */
    suspend fun getVehicles(): Result<List<Vehicle>> {
        return try {
            val snapshot = vehiclesCollection.get().await()
            val vehicleList: List<Vehicle> = snapshot.documents.mapNotNull { doc: DocumentSnapshot ->
                val make = doc.getString("make") ?: ""
                val model = doc.getString("model") ?: ""
                val year = doc.getLong("year")?.toInt() ?: doc.getString("year")?.toIntOrNull() ?: 2020
                val regNo = doc.getString("registrationNumber") ?: ""
                val fuelType = doc.getString("fuelType") ?: "Petrol"
                val mileage = doc.getLong("currentMileage")?.toInt()
                    ?: doc.getLong("currentOdometer")?.toInt()
                    ?: 0

                Vehicle(
                    id = doc.id,
                    userId = "default_user",
                    make = make,
                    model = model,
                    year = year,
                    registrationNumber = regNo,
                    vin = doc.getString("vin") ?: "",
                    fuelType = fuelType,
                    currentOdometer = mileage,
                    serviceIntervalKm = doc.getLong("serviceIntervalKm")?.toInt() ?: 15000,
                    isDefault = false
                )
            }
            Log.d("DrivePulseFirestore", "Retrieved ${vehicleList.size} vehicles from Firestore")
            Result.success(vehicleList)
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Error retrieving vehicles from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a vehicle document from Cloud Firestore.
     */
    suspend fun deleteVehicle(vehicleId: String): Result<Unit> {
        return try {
            vehiclesCollection.document(vehicleId).delete().await()
            Log.d("DrivePulseFirestore", "Deleted vehicle $vehicleId from Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Error deleting vehicle from Firestore", e)
            Result.failure(e)
        }
    }
}
