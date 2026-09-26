package com.example.drivepulse

import com.example.drivepulse.util.NetworkResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkResultTest {

    @Test
    fun testNetworkResultSuccess() {
        val result = NetworkResult.Success("Test Data")
        assertTrue(result is NetworkResult.Success)
        assertEquals("Test Data", result.data)
        assertNull(result.message)
    }

    @Test
    fun testNetworkResultError() {
        val message = "Unable to connect to DrivePulse. Please check your internet connection."
        val result = NetworkResult.Error<String>(message)
        assertTrue(result is NetworkResult.Error)
        assertEquals(message, result.message)
        assertNull(result.data)
    }

    @Test
    fun testNetworkResultLoading() {
        val result = NetworkResult.Loading<String>()
        assertTrue(result is NetworkResult.Loading)
        assertNull(result.data)
        assertNull(result.message)
    }
}
