package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "Completed", "In Progress"
    val category: String, // "Grammar", "Vocabulary", "Speaking", "Reading", "Listening"
    val score: Int, // Percentage score or progress percentage
    val totalQuestions: Int,
    val timeSpentSec: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val iconEmoji: String
)
