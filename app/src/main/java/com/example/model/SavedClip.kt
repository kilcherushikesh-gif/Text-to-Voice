package com.example.model

data class SavedClip(
    val id: String,
    val title: String,
    val text: String,
    val language: IndianLanguage,
    val voiceName: String,
    val speechRate: Float,
    val pitch: Float,
    val filePath: String,
    val createdAtMillis: Long,
    val durationSeconds: Float = 0f,
    val format: String = "MP3",
    val persona: String = "Natural"
)
