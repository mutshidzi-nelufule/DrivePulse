package com.example.drivepulse.data.repository

import android.content.Context
import com.example.drivepulse.data.local.SessionManager
import com.example.drivepulse.data.model.User
import com.example.drivepulse.data.remote.api.RetrofitClient
import com.example.drivepulse.data.remote.dto.LoginRequestDto
import com.example.drivepulse.data.remote.dto.RegisterRequestDto
import com.example.drivepulse.util.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Repository for managing user authentication, registration, login, and sessions.
 *
 * Authentication is handled through the Spring Boot REST API.
 * The SessionManager keeps track of the currently logged-in user locally.
 */
class AuthRepository(context: Context) {

    private val sessionManager = SessionManager(context)

    private val api = RetrofitClient.apiService

    /**
     * Registers a new user through the REST API.
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult = withContext(Dispatchers.IO) {

        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim().lowercase()

        // Local validation before sending data to the API
        if (!ValidationUtils.isValidName(trimmedName)) {
            return@withContext AuthResult.Error(
                "Please enter a valid full name (at least 2 characters)."
            )
        }

        if (!ValidationUtils.isValidEmail(trimmedEmail)) {
            return@withContext AuthResult.Error(
                "Please enter a valid email address."
            )
        }

        if (!ValidationUtils.isValidPassword(password)) {
            return@withContext AuthResult.Error(
                "Password must be at least 6 characters long and include letters and numbers."
            )
        }

        if (password != confirmPassword) {
            return@withContext AuthResult.Error(
                "Passwords do not match."
            )
        }

        try {
            val response = api.registerUser(
                RegisterRequestDto(
                    name = trimmedName,
                    email = trimmedEmail,
                    password = password
                )
            )

            if (response.isSuccessful) {

                val dto = response.body()

                if (dto != null) {

                    val user = User(
                        id = dto.id.toString(),
                        fullName = dto.name,
                        email = dto.email,
                        passwordHash = ""
                    )

                    // Save the logged-in session locally
                    sessionManager.saveUserSession(user)

                    AuthResult.Success(user)

                } else {
                    AuthResult.Error(
                        "Registration failed: empty response from server."
                    )
                }

            } else {

                val errorMessage = when (response.code()) {
                    400 -> "Registration failed. The email may already be registered."
                    409 -> "An account with this email already exists."
                    else -> "Registration failed. Server error: ${response.code()}"
                }

                AuthResult.Error(errorMessage)
            }

        } catch (e: Exception) {

            AuthResult.Error(
                "Unable to connect to the server. Please make sure the DrivePulse API is running."
            )
        }
    }

    /**
     * Authenticates an existing user through the REST API.
     */
    suspend fun login(
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {

        val trimmedEmail = email.trim().lowercase()

        if (trimmedEmail.isBlank()) {
            return@withContext AuthResult.Error(
                "Please enter your email address."
            )
        }

        if (password.isBlank()) {
            return@withContext AuthResult.Error(
                "Please enter your password."
            )
        }

        try {

            val response = api.loginUser(
                LoginRequestDto(
                    email = trimmedEmail,
                    password = password
                )
            )

            if (response.isSuccessful) {

                val dto = response.body()

                if (dto != null) {

                    val user = User(
                        id = dto.id.toString(),
                        fullName = dto.name,
                        email = dto.email,
                        passwordHash = ""
                    )

                    // Save session locally
                    sessionManager.saveUserSession(user)

                    AuthResult.Success(user)

                } else {

                    AuthResult.Error(
                        "Login failed: empty response from server."
                    )
                }

            } else {

                when (response.code()) {

                    401 -> AuthResult.Error(
                        "Invalid email or password. Please try again."
                    )

                    400 -> AuthResult.Error(
                        "Please enter a valid email and password."
                    )

                    else -> AuthResult.Error(
                        "Login failed. Server error: ${response.code()}"
                    )
                }
            }

        } catch (e: Exception) {

            AuthResult.Error(
                "Unable to connect to the server. Please make sure the DrivePulse API is running."
            )
        }
    }

    /**
     * Logs out the current user session.
     */
    fun logout() {
        sessionManager.clearSession()
    }

    /**
     * Checks if a user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    /**
     * Gets the currently logged-in user.
     */
    fun getCurrentUser(): User? {
        return sessionManager.getLoggedInUser()
    }
}
