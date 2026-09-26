package com.example.drivepulse.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        VehicleEntity::class,
        FuelRecordEntity::class,
        MaintenanceEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DrivePulseDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun fuelDao(): FuelDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: DrivePulseDatabase? = null

        fun getDatabase(context: Context): DrivePulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrivePulseDatabase::class.java,
                    "drivepulse_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
