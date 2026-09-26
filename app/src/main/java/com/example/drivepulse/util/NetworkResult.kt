package com.example.drivepulse.util

import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data = data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data = data, message = message)
    class Loading<T> : NetworkResult<T>()
}

object NetworkUtils {
    /**
     * Helper to wrap Retrofit API calls safely with user-friendly error messages.
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error("Empty response received from server.")
                }
            } else {
                val code = response.code()
                val errorMsg = when (code) {
                    401 -> "Unauthorized. Please log in again."
                    403 -> "Access forbidden."
                    404 -> "Requested resource not found."
                    500 -> "Server error. Please try again later."
                    else -> "HTTP Error: $code"
                }
                NetworkResult.Error(errorMsg)
            }
        } catch (e: UnknownHostException) {
            NetworkResult.Error("Unable to connect to DrivePulse. Please check your internet connection.")
        } catch (e: SocketTimeoutException) {
            NetworkResult.Error("Connection timed out. Please try again.")
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
