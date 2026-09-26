package com.example.drivepulse.util

import com.example.drivepulse.data.model.MaintenanceRecord
import com.example.drivepulse.data.model.Vehicle

data class HealthScoreResult(
    val score: Int,
    val statusText: String,
    val summary: String,
    val recommendations: List<String>
)

object HealthScoreCalculator {

    /**
     * Calculates DrivePulse Vehicle Health Score (0 - 100).
     */
    fun calculateHealthScore(
        vehicle: Vehicle?,
        maintenanceRecords: List<MaintenanceRecord>
    ): HealthScoreResult {
        if (vehicle == null) {
            return HealthScoreResult(
                score = 100,
                statusText = "No Vehicle",
                summary = "Please add a vehicle to view health status.",
                recommendations = listOf("Add a vehicle in Vehicles screen.")
            )
        }

        var score = 100
        val recommendations = mutableListOf<String>()

        if (maintenanceRecords.isEmpty()) {
            score -= 15
            recommendations.add("No maintenance records logged yet. Log recent service to keep history updated.")
        } else {
            for (record in maintenanceRecords) {
                if (record.nextServiceOdometer > 0) {
                    val remainingKm = record.nextServiceOdometer - vehicle.currentOdometer
                    if (remainingKm <= 0) {
                        score -= 20
                        recommendations.add("Overdue service: ${record.serviceType} (Due at ${record.nextServiceOdometer} km).")
                    } else if (remainingKm in 1..500) {
                        score -= 10
                        recommendations.add("Upcoming service due soon: ${record.serviceType} in $remainingKm km.")
                    }
                }
            }

            // High Mileage Check
            if (vehicle.currentOdometer > 150000) {
                val latestMaintenanceOdometer = maintenanceRecords.maxOfOrNull { it.odometer } ?: 0
                if (vehicle.currentOdometer - latestMaintenanceOdometer > 10000) {
                    score -= 10
                    recommendations.add("High mileage vehicle: Consider scheduling a comprehensive inspection.")
                }
            }
        }

        // Clamp score between 0 and 100
        val finalScore = score.coerceIn(0, 100)

        val (statusText, summary) = when (finalScore) {
            in 85..100 -> "Excellent" to "Your vehicle is in top maintenance condition."
            in 70..84 -> "Good" to "Vehicle condition is good. Monitor upcoming service items."
            in 50..69 -> "Fair" to "Service required soon to maintain optimal vehicle health."
            else -> "Attention Required" to "Overdue services or missing records detected!"
        }

        if (recommendations.isEmpty()) {
            recommendations.add("All services up to date! Continue regular maintenance checks.")
        }

        return HealthScoreResult(
            score = finalScore,
            statusText = statusText,
            summary = summary,
            recommendations = recommendations
        )
    }
}
