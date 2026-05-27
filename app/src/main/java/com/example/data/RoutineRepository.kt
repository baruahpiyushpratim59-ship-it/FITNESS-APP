package com.example.data

import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.model.RoutineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class RoutineRepository(private val routineDao: RoutineDao) {

    fun getRoutinesForDate(date: String): Flow<List<RoutineItem>> =
        routineDao.getRoutinesForDate(date)

    suspend fun insert(item: RoutineItem) = withContext(Dispatchers.IO) {
        routineDao.insertRoutine(item)
    }

    suspend fun update(item: RoutineItem) = withContext(Dispatchers.IO) {
        routineDao.updateRoutine(item)
    }

    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        routineDao.deleteRoutineById(id)
    }

    suspend fun prepopulateIfEmpty(date: String) = withContext(Dispatchers.IO) {
        if (!routineDao.hasRoutinesForDate(date)) {
            val defaults = listOf(
                RoutineItem(
                    title = "Wake Up & Rise",
                    category = "wakeup",
                    scheduledTime = "06:30 AM",
                    notes = "Take a deep breath and greet the day. Avoid checks on your phone.",
                    dateString = date
                ),
                RoutineItem(
                    title = "Warm Elixir Start",
                    category = "morning",
                    scheduledTime = "06:45 AM",
                    notes = "Drink a warm glass of water to hydrate and boost metabolism.",
                    dateString = date
                ),
                RoutineItem(
                    title = "5-Minute Power Flow",
                    category = "exercise",
                    scheduledTime = "07:00 AM",
                    notes = "A quick round of jumping jacks, squats, and a refreshing full-body stretch setup for energy.",
                    dateString = date
                ),
                RoutineItem(
                    title = "Mindful Protein Fuel",
                    category = "breakfast",
                    scheduledTime = "07:30 AM",
                    notes = "Oatmeal with almonds & half a banana or eggs. Nutritious energy.",
                    dateString = date
                ),
                RoutineItem(
                    title = "Deep Focus Study Block",
                    category = "study",
                    scheduledTime = "09:00 AM",
                    notes = "First focus block of study or work. Silence notifications and execute.",
                    dateString = date
                ),
                RoutineItem(
                    title = "Drink Water Tracker",
                    category = "water",
                    completedCount = 0,
                    targetCount = 8,
                    notes = "Aim for at least 8 cups (250ml each) spaced out evenly.",
                    dateString = date
                ),
                RoutineItem(
                    title = "Afternoon Revision Block",
                    category = "study",
                    scheduledTime = "02:30 PM",
                    notes = "Review notes, flashcards, or practice active recall.",
                    dateString = date
                ),
                RoutineItem(
                    title = "Screen Wind-Down",
                    category = "sleep",
                    scheduledTime = "10:00 PM",
                    notes = "Turn off phone/laptop screen. Read a book or listen to calming music.",
                    dateString = date
                ),
                RoutineItem(
                    title = "Restorative Sleep",
                    category = "sleep",
                    scheduledTime = "10:30 PM",
                    notes = "Set back up alarm, keep room cool, and drift off into peaceful sleep.",
                    dateString = date
                )
            )
            routineDao.insertRoutines(defaults)
        }
    }

    suspend fun fetchAIMotivationalQuote(): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", true)) {
            return@withContext getLocalFallbackQuote()
        }

        val prompt = "Generate a single short, extremely powerful, and modern motivational quote for starting a productive routine. Return ONLY the quote text and the author name (e.g. 'Quote text' — Author). Do not add introductory conversational chat."
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: getLocalFallbackQuote()
        } catch (e: Exception) {
            e.printStackTrace()
            getLocalFallbackQuote()
        }
    }

    suspend fun fetchAISuggestion(category: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", true)) {
            return@withContext getLocalFallbackSuggestion(category)
        }

        val prompt = when (category) {
            "exercise" -> "Suggest a rapid 5-minute home workout or yoga flow. Return a bulleted outline of 3-4 moves that require no equipment. Be highly encouraging and under 80 words."
            "breakfast" -> "Suggest a delicious and minimal healthy breakfast that can be prepped in 10 minutes or less. Focus on clean protein and fiber, under 80 words."
            "study" -> "Give a single highly effective productivity hack or planning advice for a modern student (e.g. Pomodoro variations, Active Recall). Keep it under 80 words."
            else -> "Suggest a tiny, impactful habit to incorporate into the daily morning or evening flow. Keep it under 80 words."
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: getLocalFallbackSuggestion(category)
        } catch (e: Exception) {
            e.printStackTrace()
            getLocalFallbackSuggestion(category)
        }
    }

    private fun getLocalFallbackQuote(): String {
        val quotes = listOf(
            "\"The secret of your future is hidden in your daily routine.\" — Mike Murdock",
            "\"Your future is created by what you do today, not tomorrow.\" — Anonymous",
            "\"Small daily improvements over time lead to stunning results.\" — Robin Sharma",
            "\"You will never change your life until you change something you do daily.\" — John C. Maxwell",
            "\"Focus on progress, not perfection.\" — Anonymous",
            "\"Make today your masterpiece.\" — John Wooden",
            "\"Energy and persistence conquer all things.\" — Benjamin Franklin"
        )
        return quotes.random()
    }

    private fun getLocalFallbackSuggestion(category: String): String {
        return when (category) {
            "exercise" -> "• Jumping Jacks (1 min) to warm up.\n• Bodyweight Squats (2 mins) to build strength.\n• Push-ups/Knee push-ups (1 min) for upper body.\n• Cobra stretch & Child Pose (1 min)."
            "breakfast" -> "🥑 Avocado Mash Toast & Boiled Eggs:\nToasted whole-grain bread topped with avocado and two boiled eggs. Generous protein, healthy fats, and fiber to fuel your cognitive focus!"
            "study" -> "🍅 Rule of 25:5 (Pomodoro):\nCommit to studying for exactly 25 minutes with absolutely zero distractions, followed by a strict 5-minute movement break. Restarts mental energy."
            else -> "✨ Tiny habit loop:\nAfter you take your first sip of water, write down your absolute #1 priority task for the day on a scrap piece of paper."
        }
    }
}
