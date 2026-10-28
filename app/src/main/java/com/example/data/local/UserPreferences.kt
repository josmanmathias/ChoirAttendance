package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sion_choir_session")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_ID = intPreferencesKey("user_id")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_NAMA_LENGKAP = stringPreferencesKey("nama_lengkap")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_NO_HP = stringPreferencesKey("no_hp")
        val KEY_POSISI = stringPreferencesKey("posisi")
        val KEY_ROLE = stringPreferencesKey("role")
        val KEY_FOTO = stringPreferencesKey("foto")

        val KEY_BIBLE_POINTS = intPreferencesKey("bible_points")
        val KEY_DAILY_STREAK = intPreferencesKey("daily_streak")
        val KEY_WEEKLY_STREAKS = intPreferencesKey("weekly_streaks")
        val KEY_MONTHLY_STREAKS = intPreferencesKey("monthly_streaks")
        val KEY_LAST_STRIKE_DATE = stringPreferencesKey("last_strike_date")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val currentUser: Flow<User?> = context.dataStore.data.map { prefs ->
        val loggedIn = prefs[KEY_IS_LOGGED_IN] ?: false
        if (!loggedIn) null
        else {
            User(
                id = prefs[KEY_USER_ID] ?: 1,
                username = prefs[KEY_USERNAME] ?: "",
                namaLengkap = prefs[KEY_NAMA_LENGKAP] ?: "Anggota Sion Choir",
                email = prefs[KEY_EMAIL],
                noHp = prefs[KEY_NO_HP],
                posisi = prefs[KEY_POSISI] ?: "Sopran",
                role = prefs[KEY_ROLE] ?: "member",
                foto = prefs[KEY_FOTO],
                status = "active"
            )
        }
    }

    val biblePoints: Flow<Int> = context.dataStore.data.map { it[KEY_BIBLE_POINTS] ?: 15 }
    val dailyStreak: Flow<Int> = context.dataStore.data.map { it[KEY_DAILY_STREAK] ?: 3 }
    val weeklyStreaks: Flow<Int> = context.dataStore.data.map { it[KEY_WEEKLY_STREAKS] ?: 1 }
    val monthlyStreaks: Flow<Int> = context.dataStore.data.map { it[KEY_MONTHLY_STREAKS] ?: 0 }

    suspend fun saveSession(user: User) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_USERNAME] = user.username
            prefs[KEY_NAMA_LENGKAP] = user.namaLengkap
            prefs[KEY_EMAIL] = user.email ?: ""
            prefs[KEY_NO_HP] = user.noHp ?: ""
            prefs[KEY_POSISI] = user.posisi
            prefs[KEY_ROLE] = user.role
            prefs[KEY_FOTO] = user.foto ?: ""
        }
    }

    suspend fun addPoints(points: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_BIBLE_POINTS] ?: 15
            prefs[KEY_BIBLE_POINTS] = current + points
        }
    }

    suspend fun recordDailyStrike(dateStr: String) {
        context.dataStore.edit { prefs ->
            val lastDate = prefs[KEY_LAST_STRIKE_DATE] ?: ""
            if (lastDate != dateStr) {
                prefs[KEY_LAST_STRIKE_DATE] = dateStr
                val currentDaily = (prefs[KEY_DAILY_STREAK] ?: 0) + 1
                prefs[KEY_DAILY_STREAK] = currentDaily
                val pts = (prefs[KEY_BIBLE_POINTS] ?: 15) + 5
                prefs[KEY_BIBLE_POINTS] = pts

                // Check weekly strike (every 7 days)
                if (currentDaily % 7 == 0) {
                    val wCount = (prefs[KEY_WEEKLY_STREAKS] ?: 0) + 1
                    prefs[KEY_WEEKLY_STREAKS] = wCount
                    prefs[KEY_BIBLE_POINTS] = pts + 10 // additional 10 pts

                    // Check monthly strike (4 consecutive weekly strikes)
                    if (wCount % 4 == 0) {
                        val mCount = (prefs[KEY_MONTHLY_STREAKS] ?: 0) + 1
                        prefs[KEY_MONTHLY_STREAKS] = mCount
                        prefs[KEY_BIBLE_POINTS] = pts + 10 + 20 // additional 20 pts
                    }
                }
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_NAMA_LENGKAP)
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_NO_HP)
            prefs.remove(KEY_POSISI)
            prefs.remove(KEY_ROLE)
            prefs.remove(KEY_FOTO)
        }
    }
}
