package com.example.drivepulse

import com.example.drivepulse.util.ValidationUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidationTest {

    @Test
    fun testValidEmailFormat() {
        assertTrue(ValidationUtils.isValidEmail("student@university.ac.za"))
        assertTrue(ValidationUtils.isValidEmail("user.name@drivepulse.com"))
    }

    @Test
    fun testInvalidEmailFormat() {
        assertFalse(ValidationUtils.isValidEmail("invalid-email"))
        assertFalse(ValidationUtils.isValidEmail("user@com"))
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun testPasswordRequirements() {
        assertTrue(ValidationUtils.isValidPassword("Password123"))
        assertTrue(ValidationUtils.isValidPassword("abc123"))

        // Invalid cases
        assertFalse(ValidationUtils.isValidPassword("12345")) // Too short
        assertFalse(ValidationUtils.isValidPassword("abcdef")) // No digits
        assertFalse(ValidationUtils.isValidPassword("123456")) // No letters
    }

    @Test
    fun testPasswordHashing() {
        val plainTextPassword = "SecurePassword123"
        val hash1 = ValidationUtils.hashPassword(plainTextPassword)
        val hash2 = ValidationUtils.hashPassword(plainTextPassword)

        // Ensure hash is consistent
        assertEquals(hash1, hash2)

        // Ensure hash is NOT equal to plain text password (security check)
        assertNotEquals(plainTextPassword, hash1)

        // Verify SHA-256 string length (64 hex characters)
        assertEquals(64, hash1.length)
    }

    @Test
    fun testNameValidation() {
        assertTrue(ValidationUtils.isValidName("John Doe"))
        assertFalse(ValidationUtils.isValidName("A"))
        assertFalse(ValidationUtils.isValidName(""))
    }
}
