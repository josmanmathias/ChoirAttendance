package com.example.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "completed_chapters", primaryKeys = ["date", "bookId", "chapter"])
data class CompletedChapterEntity(
    val date: String, // YYYY-MM-DD
    val bookId: Int,
    val chapter: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "verse_highlights", primaryKeys = ["bookId", "chapter", "verse"])
data class VerseHighlightEntity(
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val colorHex: String
)

@Entity(tableName = "rhema_notes")
data class RhemaNoteEntity(
    @PrimaryKey val id: String,
    val date: String,
    val bookId: Int,
    val chapter: Int,
    val reference: String,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_attendance")
data class LocalAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val type: String, // LATIHAN or IBADAH
    val method: String, // SELFIE or QR_CODE
    val tanggal: String,
    val waktu: String,
    val latitude: Double?,
    val longitude: Double?,
    val foto: String?
)

@Dao
interface BibleDao {
    @Query("SELECT * FROM completed_chapters WHERE date = :date")
    fun getCompletedChaptersByDate(date: String): Flow<List<CompletedChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markChapterCompleted(chapter: CompletedChapterEntity)

    @Query("SELECT COUNT(*) FROM completed_chapters WHERE date = :date")
    suspend fun getCompletedCountForDate(date: String): Int

    @Query("SELECT * FROM verse_highlights WHERE bookId = :bookId AND chapter = :chapter")
    fun getHighlights(bookId: Int, chapter: Int): Flow<List<VerseHighlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHighlight(highlight: VerseHighlightEntity)

    @Delete
    suspend fun deleteHighlight(highlight: VerseHighlightEntity)

    @Query("SELECT * FROM rhema_notes ORDER BY timestamp DESC")
    fun getAllRhemaNotes(): Flow<List<RhemaNoteEntity>>

    @Query("SELECT * FROM rhema_notes WHERE bookId = :bookId AND chapter = :chapter")
    fun getRhemaNotesForChapter(bookId: Int, chapter: Int): Flow<List<RhemaNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRhemaNote(note: RhemaNoteEntity)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM local_attendance ORDER BY id DESC")
    fun getAllAttendance(): Flow<List<LocalAttendanceEntity>>

    @Query("SELECT * FROM local_attendance WHERE type = :type ORDER BY id DESC")
    fun getAttendanceByType(type: String): Flow<List<LocalAttendanceEntity>>

    @Query("SELECT * FROM local_attendance WHERE tanggal = :today ORDER BY id DESC")
    fun getTodayAttendance(today: String): Flow<List<LocalAttendanceEntity>>

    @Query("SELECT * FROM local_attendance WHERE tanggal = :today ORDER BY id DESC")
    suspend fun getTodayAttendanceList(today: String): List<LocalAttendanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: LocalAttendanceEntity): Long
}

@Database(
    entities = [
        CompletedChapterEntity::class,
        VerseHighlightEntity::class,
        RhemaNoteEntity::class,
        LocalAttendanceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sion_choir_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
