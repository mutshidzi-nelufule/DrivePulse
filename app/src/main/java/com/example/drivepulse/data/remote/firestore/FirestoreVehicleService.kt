package com.example.drivepulse.data.remote.firestore

import android.util.Log
import com.example.drivepulse.data.model.Vehicle
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Cloud Firestore service for DrivePulse vehicle records.
 * Manages operations on the "vehicles" collection.
 */
class FirestoreVehicleService {

    private fun getDb(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("DrivePulseFirestore", "Firebase instance not ready", e)
            null
        }
    }

    suspend fun saveVehicle(vehicle: Vehicle): Result<String> = suspendCancellableCoroutine { continuation ->
        try {
            val db = getDb()
            if (db == null) {
                continuation.resume(Result.failure(Exception("Firestore not initialized")))
                return@suspendCancellableCoroutine
            }

            val docRef: DocumentReference = if (vehicle.id.isBlank()) {
                db.collection("vehicles").document()
            } else {
                db.collection("vehicles").document(vehicle.id)
            }

            val vehicleData: Map<String, Any> = mapOf(
                "make" to vehicle.make,
                "model" to vehicle.model,
                "year" to vehicle.year,
                "registrationNumber" to vehicle.registrationNumber,
                "fuelType" to vehicle.fuelType,
                "currentMileage" to vehicle.currentOdometer
            )

            docRef.set(vehicleData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("DrivePulseFirestore", "Saved vehicle ${docRef.id}")
                    if (continuation.isActive) continuation.resume(Result.success(docRef.id))
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("DrivePulseFirestore", "Failed to save vehicle", e)
                    if (continuation.isActive) continuation.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(Result.failure(e))
        }
    }

    suspend fun getVehicleById(vehicleId: String): Result<Vehicle?> = suspendCancellableCoroutine { continuation ->
        try {
            val db = getDb()
            if (db == null) {
                continuation.resume(Result.failure(Exception("Firestore not initialized")))
                return@suspendCancellableCoroutine
            }

            db.collection("vehicles").document(vehicleId).get()
                .addOnSuccessListener { doc: DocumentSnapshot? ->
                    if (doc != null && doc.exists()) {
                        val make = doc.getString("make") ?: ""
                        val model = doc.getString("model") ?: ""
                        val year = doc.getLong("year")?.toInt() ?: 2020
                        val regNo = doc.getString("registrationNumber") ?: ""
                        val fuelType = doc.getString("fuelType") ?: "Petrol"
                        val mileage = doc.getLong("currentMileage")?.toInt() ?: 0

                        val vehicle = Vehicle(
                            id = doc.id,
                            userId = "default_user",
                            make = make,
                            model = model,
                            year = year,
                            registrationNumber = regNo,
                            fuelType = fuelType,
                            currentOdometer = mileage,
                            isDefault = false
                        )
                        if (continuation.isActive) continuation.resume(Result.success(vehicle))
                    } else {
                        if (continuation.isActive) continuation.resume(Result.success(null))
                    }
                }
                .addOnFailureListener { e: Exception ->
                    if (continuation.isActive) continuation.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(Result.failure(e))
        }
    }

    suspend fun getVehicles(): Result<List<Vehicle>> = suspendCancellableCoroutine { continuation ->
        try {
            val db = getDb()
            if (db == null) {
                continuation.resume(Result.failure(Exception("Firestore not initialized")))
                return@suspendCancellableCoroutine
            }

            db.collection("vehicles").get()
                .addOnSuccessListener { snapshot: QuerySnapshot? ->
                    val list = mutableListOf<Vehicle>()
                    if (snapshot != null) {
                        val documents: List<DocumentSnapshot> = snapshot.documents
                        for (doc in documents) {
                            val make = doc.getString("make") ?: ""
                            val model = doc.getString("model") ?: ""
                            val year = doc.getLong("year")?.toInt() ?: 2020
                            val regNo = doc.getString("registrationNumber") ?: ""
                            val fuelType = doc.getString("fuelType") ?: "Petrol"
                            val mileage = doc.getLong("currentMileage")?.toInt() ?: 0

                            list.add(
                                Vehicle(
                                    id = doc.id,
                                    userId = "default_user",
                                    make = make,
                                    model = model,
                                    year = year,
                                    registrationNumber = regNo,
                                    fuelType = fuelType,
                                    currentOdometer = mileage,
                                    isDefault = false
                                )
                            )
                        }
                    }
                    if (continuation.isActive) continuation.resume(Result.success(list))
                }
                .addOnFailureListener { e: Exception ->
                    if (continuation.isActive) continuation.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(Result.failure(e))
        }
    }

    suspend fun deleteVehicle(vehicleId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        try {
            val db = getDb()
            if (db == null) {
                continuation.resume(Result.failure(Exception("Firestore not initialized")))
                return@suspendCancellableCoroutine
            }

            db.collection("vehicles").document(vehicleId).delete()
                .addOnSuccessListener {
                    if (continuation.isActive) continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e: Exception ->
                    if (continuation.isActive) continuation.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(Result.failure(e))
        }
    }
}
