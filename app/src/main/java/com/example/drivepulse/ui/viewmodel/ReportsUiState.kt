package com.example.drivepulse.ui.viewmodel

import com.example.drivepulse.util.ReportSummary

data class ReportsUiState(
    val reportSummary: ReportSummary? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
