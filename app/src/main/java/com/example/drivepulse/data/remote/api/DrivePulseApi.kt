package com.example.drivepulse.data.remote.api

import com.example.drivepulse.data.remote.dto.AuthResponseDto
import com.example.drivepulse.data.remote.dto.FuelRecordDto
import com.example.drivepulse.data.remote.dto.LoginRequestDto
import com.example.drivepulse.data.remote.dto.MaintenanceRecordDto
import com.example.drivepulse.data.remote.dto.RegisterRequestDto
import com.example.drivepulse.data.remote.dto.VehicleDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DrivePulseApi {

    @POST("api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @GET("api/vehicles")
    suspend fun getVehicles(): Response<List<VehicleDto>>

    @POST("api/vehicles")
    suspend fun addVehicle(@Body vehicle: VehicleDto): Response<VehicleDto>

    @PUT("api/vehicles/{id}")
    suspend fun updateVehicle(@Path("id") id: String, @Body vehicle: VehicleDto): Response<VehicleDto>

    @DELETE("api/vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: String): Response<Unit>

    @GET("api/fuel")
    suspend fun getFuelRecords(@Query("vehicleId") vehicleId: String): Response<List<FuelRecordDto>>

    @POST("api/fuel")
    suspend fun addFuelRecord(@Body record: FuelRecordDto): Response<FuelRecordDto>

    @GET("api/maintenance")
    suspend fun getMaintenanceRecords(@Query("vehicleId") vehicleId: String): Response<List<MaintenanceRecordDto>>

    @POST("api/maintenance")
    suspend fun addMaintenanceRecord(@Body record: MaintenanceRecordDto): Response<MaintenanceRecordDto>
}
