package com.example

import com.example.data.local.BibleDataProvider
import com.example.data.model.ChurchLocation
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.Date

class ExampleUnitTest {

    @Test
    fun testBibleBooksIntegrity() {
        val books = BibleDataProvider.allBooks
        assertEquals(66, books.size)

        val plBooks = books.filter { it.testament == "PL" }
        val pbBooks = books.filter { it.testament == "PB" }

        assertEquals(39, plBooks.size)
        assertEquals(27, pbBooks.size)

        // Check specific books from ba5_books
        assertEquals("Kejadian", books[0].name)
        assertEquals("Mazmur", books[18].name)
        assertEquals(150, books[18].chapterCount)
        assertEquals("Amsal", books[19].name)
        assertEquals(31, books[19].chapterCount)
        assertEquals("Matius", books[39].name)
        assertEquals("Wahyu", books[65].name)
        assertEquals(22, books[65].chapterCount)
    }

    @Test
    fun testDailyReadingPlanStructure() {
        val testDate = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 27)
        }.time

        val plan = BibleDataProvider.getPlanForDate(testDate)
        assertEquals(5, plan.chapters.size)

        // 2 PL, 1 PB, 1 Mazmur, 1 Amsal
        val plChapters = plan.chapters.filter { it.category == "Perjanjian Lama" }
        val pbChapters = plan.chapters.filter { it.category == "Perjanjian Baru" }
        val mazmurChapters = plan.chapters.filter { it.category == "Mazmur" }
        val amsalChapters = plan.chapters.filter { it.category == "Amsal" }

        assertEquals(2, plChapters.size)
        assertEquals(1, pbChapters.size)
        assertEquals(1, mazmurChapters.size)
        assertEquals(1, amsalChapters.size)

        // On day 27 of the month, Amsal chapter must be 27
        assertEquals(27, amsalChapters[0].chapter)
    }

    @Test
    fun testChurchLocationCoordinates() {
        val church = ChurchLocation()
        assertEquals(-6.728251, church.latitude, 0.000001)
        assertEquals(108.553246, church.longitude, 0.000001)
        assertEquals(100f, church.allowedRadiusMeters, 0.01f)
    }
}
