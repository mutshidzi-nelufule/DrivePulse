package com.example.drivepulse.data.remote.dto

data class DocumentUploadResponseDto(
    val fileName: String = "",
    val storagePath: String = "",
    val fileType: String = "",
    val fileSize: Long = 0,
    val publicUrl: String = "",
    val status: String = ""
)
