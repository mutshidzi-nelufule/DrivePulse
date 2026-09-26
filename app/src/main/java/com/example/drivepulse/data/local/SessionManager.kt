package com.example.drivepulse.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.drivepulse.data.model.User

/**
 * Manages persistent user session and authentication state across app restarts.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "drivepulse_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }

    /**
     * Saves user session to SharedPreferences upon successful login/registration.
     */
    fun saveUserSession(user: User) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.fullName)
            putString(KEY_USER_EMAIL, user.email)
            apply()
        }
    }

    /**
     * Checks if user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Retrieves currently logged in user session details if available.
     */
    fun getLoggedInUser(): User? {
        if (!isLoggedIn()) return null
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val name = prefs.getString(KEY_USER_NAME, "") ?: ""
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        return User(id = id, fullName = name, email = email, passwordHash = "")
    }

    /**
     * Clears user session on logout.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
