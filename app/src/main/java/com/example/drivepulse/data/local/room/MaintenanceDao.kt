package com.example.drivepulse.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM maintenance_records WHERE vehicleId = :vehicleId ORDER BY odometer DESC")
    fun getMaintenanceForVehicle(vehicleId: String): Flow<List<MaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(record: MaintenanceEntity)

    @Query("DELETE FROM maintenance_records WHERE id = :id")
    suspend fun deleteMaintenanceById(id: String)
}
