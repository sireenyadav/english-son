package com.example.data.model

data class EnglishTest(
    val id: String,
    val title: String,
    val category: String, // "Grammar", "Vocabulary", "Speaking", "Reading"
    val durationMinutes: Int,
    val questionsCount: Int,
    val questions: List<Question>,
    val description: String,
    val iconEmoji: String,
    val readingPassage: String? = null // For Reading Tests
)
