package com.example.drivepulse.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelDao {

    @Query("SELECT * FROM fuel_records WHERE vehicleId = :vehicleId ORDER BY odometer DESC")
    fun getFuelRecordsForVehicle(vehicleId: String): Flow<List<FuelRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelRecord(record: FuelRecordEntity)

    @Query("DELETE FROM fuel_records WHERE id = :id")
    suspend fun deleteFuelRecordById(id: String)
}
