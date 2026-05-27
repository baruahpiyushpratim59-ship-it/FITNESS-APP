package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_items")
data class RoutineItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "morning", "wakeup", "exercise", "breakfast", "study", "water", "sleep", "custom"
    val isCompleted: Boolean = false,
    val scheduledTime: String = "", // e.g., "07:00 AM"
    val completedCount: Int = 0, // e.g., cups of water
    val targetCount: Int = 8, // target cups of water
    val notes: String = "", // dynamic description / suggestions
    val dateString: String // e.g., "2026-05-25"
)
