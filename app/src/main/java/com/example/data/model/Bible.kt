package com.example.data.model

data class BibleBook(
    val id: Int,
    val name: String,
    val testament: String, // 'PL', 'PB'
    val chapterCount: Int
)

data class BibleVerse(
    val id: Int,
    val version: String = "TB",
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)

data class DailyReadingPlan(
    val dayOfYear: Int,
    val dateDisplay: String,
    val chapters: List<ReadingChapterTarget>
)

data class ReadingChapterTarget(
    val bookId: Int,
    val bookName: String,
    val chapter: Int,
    val category: String, // 'Perjanjian Lama', 'Perjanjian Baru', 'Mazmur', 'Amsal'
    val isCompleted: Boolean = false
)

data class VerseHighlight(
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val colorHex: String // e.g. "#FEF08A", "#BBF7D0", "#BAE6FD", "#FBCFE8"
)

data class RhemaNote(
    val id: String,
    val date: String,
    val title: String,
    val reference: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ReadingStreakInfo(
    val points: Int = 0,
    val currentDailyStreak: Int = 0,
    val weeklyStrikeCount: Int = 0,
    val monthlyStrikeCount: Int = 0,
    val completedDays: Set<String> = emptySet(),
    val completedChapters: Set<String> = emptySet() // "bookId_chapter_date"
)
