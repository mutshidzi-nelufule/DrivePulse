package com.example.drivepulse.util

import java.security.MessageDigest

object SecurityUtils {

    /**
     * Hashes plain text password securely using SHA-256 algorithm.
     * Prevents storing plain text passwords.
     */
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Validates email address structure.
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return email.trim().matches(emailRegex)
    }

    /**
     * Validates password strength:
     * - Minimum 6 characters
     * - Must contain at least 1 letter and 1 digit
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 6) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    /**
     * Validates full name input.
     */
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }
}
