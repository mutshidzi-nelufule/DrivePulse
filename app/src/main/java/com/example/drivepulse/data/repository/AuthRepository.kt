package com.example.drivepulse.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.drivepulse.data.local.SessionManager
import com.example.drivepulse.data.model.User
import com.example.drivepulse.data.remote.api.ApiClient
import com.example.drivepulse.data.remote.dto.LoginRequestDto
import com.example.drivepulse.data.remote.dto.RegisterRequestDto
import com.example.drivepulse.data.remote.firebase.GoogleAuthClient
import com.example.drivepulse.util.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Repository managing user authentication, registration, login, and session persistence.
 *
 * Supports remote Spring Boot REST API authentication with local fallback credentials
 * storage so the app works seamlessly both online and offline.
 */
class AuthRepository(context: Context) {

    private val sessionManager = SessionManager(context)
    private val userPrefs: SharedPreferences = context.getSharedPreferences("drivepulse_registered_users", Context.MODE_PRIVATE)
    private val api = ApiClient.api

    /**
     * Registers a new user.
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult = withContext(Dispatchers.IO) {

        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim().lowercase()

        // Validation
        if (!ValidationUtils.isValidName(trimmedName)) {
            return@withContext AuthResult.Error("Please enter a valid full name (at least 2 characters).")
        }
        if (!ValidationUtils.isValidEmail(trimmedEmail)) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (!ValidationUtils.isValidPassword(password)) {
            return@withContext AuthResult.Error("Password must be at least 6 characters long and include letters and numbers.")
        }
        if (password != confirmPassword) {
            return@withContext AuthResult.Error("Passwords do not match.")
        }

        // Try API registration first
        try {
            val response = api.registerUser(
                RegisterRequestDto(
                    name = trimmedName,
                    email = trimmedEmail,
                    password = password
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val user = User(
                    id = dto.id.toString(),
                    fullName = dto.name,
                    email = dto.email,
                    passwordHash = ValidationUtils.hashPassword(password)
                )
                saveUserLocally(user, password)
                sessionManager.saveUserSession(user)
                return@withContext AuthResult.Success(user)
            }
        } catch (e: Exception) {
            // API unreachable - fallback to offline/local account creation
        }

        // Check local users
        if (userPrefs.contains("user_hash_$trimmedEmail")) {
            return@withContext AuthResult.Error("An account with this email address already exists.")
        }

        val passwordHash = ValidationUtils.hashPassword(password)
        val userId = UUID.randomUUID().toString()

        val newUser = User(
            id = userId,
            fullName = trimmedName,
            email = trimmedEmail,
            passwordHash = passwordHash
        )

        saveUserLocally(newUser, password)
        sessionManager.saveUserSession(newUser)
        return@withContext AuthResult.Success(newUser)
    }

    /**
     * Authenticates existing user.
     */
    suspend fun login(
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {

        val trimmedEmail = email.trim().lowercase()

        if (trimmedEmail.isBlank()) {
            return@withContext AuthResult.Error("Please enter your email address.")
        }
        if (password.isBlank()) {
            return@withContext AuthResult.Error("Please enter your password.")
        }

        // Try API login
        try {
            val response = api.loginUser(
                LoginRequestDto(
                    email = trimmedEmail,
                    password = password
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val user = User(
                    id = dto.id.toString(),
                    fullName = dto.name,
                    email = dto.email,
                    passwordHash = ValidationUtils.hashPassword(password)
                )
                saveUserLocally(user, password)
                sessionManager.saveUserSession(user)
                return@withContext AuthResult.Success(user)
            }
        } catch (e: Exception) {
            // API unreachable - fallback to local login
        }

        // Local credentials check
        val storedHash = userPrefs.getString("user_hash_$trimmedEmail", null)
        if (storedHash == null) {
            return@withContext AuthResult.Error("No account found with this email. Please register first.")
        }

        val enteredHash = ValidationUtils.hashPassword(password)
        if (storedHash != enteredHash) {
            return@withContext AuthResult.Error("Invalid email or password. Please try again.")
        }

        val name = userPrefs.getString("user_name_$trimmedEmail", "DrivePulse User") ?: "DrivePulse User"
        val userId = userPrefs.getString("user_id_$trimmedEmail", UUID.randomUUID().toString()) ?: UUID.randomUUID().toString()

        val user = User(
            id = userId,
            fullName = name,
            email = trimmedEmail,
            passwordHash = storedHash
        )

        sessionManager.saveUserSession(user)
        return@withContext AuthResult.Success(user)
    }

    private fun saveUserLocally(user: User, rawPassword: String) {
        val passwordHash = ValidationUtils.hashPassword(rawPassword)
        userPrefs.edit().apply {
            putString("user_hash_${user.email.lowercase()}", passwordHash)
            putString("user_name_${user.email.lowercase()}", user.fullName)
            putString("user_id_${user.email.lowercase()}", user.id)
            commit()
        }
    }

    /**
     * Authenticates user using Google Single Sign-On (SSO).
     */
    suspend fun signInWithGoogle(context: Context): AuthResult = withContext(Dispatchers.IO) {
        val googleAuthClient = GoogleAuthClient(context)
        val result = googleAuthClient.signInWithGoogle()
        if (result is AuthResult.Success) {
            sessionManager.saveUserSession(result.user)
            saveUserLocally(result.user, "GoogleSSO")
        }
        return@withContext result
    }

    /**
     * Logs out the current user session.
     */
    fun logout() {
        sessionManager.clearSession()
        try {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // Logged out
        }
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getCurrentUser(): User? = sessionManager.getLoggedInUser()
}
