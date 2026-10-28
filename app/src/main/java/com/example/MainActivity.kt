package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.local.ChoirAudioPlayer
import com.example.data.local.UserPreferences
import com.example.data.model.AttendanceRecord
import com.example.data.model.SermonCategory
import com.example.data.repository.AttendanceRepository
import com.example.data.repository.ContentRepository
import com.example.ui.components.ChoirBottomNavigation
import com.example.ui.components.MainTab
import com.example.ui.screens.*
import com.example.ui.theme.SionChoirTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen {
    data object Splash : Screen()
    data object Login : Screen()
    data class Main(val activeTab: MainTab = MainTab.BERANDA) : Screen()
    data object KotbahRaya : Screen()
    data object KotbahPW : Screen()
    data object PesanGembala : Screen()
    data object MateriPelayanan : Screen()
    data object Jadwal : Screen()
    data object RekapAbsensi : Screen()
    data class BibleReader(val bookId: Int, val chapter: Int) : Screen()
    data object RhemaJournal : Screen()
    data object PresensiFlow : Screen()
    data object AdminDashboard : Screen()
}

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userPrefs: UserPreferences
    private lateinit var repository: AttendanceRepository
    private lateinit var contentRepo: ContentRepository
    private lateinit var audioPlayer: ChoirAudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = AppDatabase.getDatabase(applicationContext)
        userPrefs = UserPreferences(applicationContext)
        repository = AttendanceRepository(db, userPrefs)
        contentRepo = ContentRepository()
        audioPlayer = ChoirAudioPlayer(applicationContext)

        setContent {
            SionChoirTheme {
                SionChoirAppRoot(
                    db = db,
                    userPrefs = userPrefs,
                    repository = repository,
                    contentRepo = contentRepo,
                    audioPlayer = audioPlayer
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.stop()
    }
}

@Composable
fun SionChoirAppRoot(
    db: AppDatabase,
    userPrefs: UserPreferences,
    repository: AttendanceRepository,
    contentRepo: ContentRepository,
    audioPlayer: ChoirAudioPlayer
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var currentMainTab by remember { mutableStateOf(MainTab.BERANDA) }

    val isLoggedIn by userPrefs.isLoggedIn.collectAsState(initial = false)
    val currentUser by userPrefs.currentUser.collectAsState(initial = null)

    val contentPosters by contentRepo.posters.collectAsState()
    val contentSermons by contentRepo.sermons.collectAsState()
    val contentMateri by contentRepo.materi.collectAsState()
    val contentGallery by contentRepo.gallery.collectAsState()
    val contentJadwalUrl by contentRepo.jadwalUrl.collectAsState()

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayAttendanceFlow = remember(todayStr) { repository.getLocalAttendanceHistory() }
    val todayRecords by todayAttendanceFlow.collectAsState(initial = emptyList())
    val filteredTodayRecords = remember(todayRecords, todayStr) {
        todayRecords.filter { it.tanggal == todayStr }
    }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            is Screen.Splash -> {
                SplashScreen(
                    onFinished = {
                        currentScreen = if (isLoggedIn) {
                            Screen.Main(MainTab.BERANDA)
                        } else {
                            Screen.Login
                        }
                    }
                )
            }

            is Screen.Login -> {
                LoginScreen(
                    repository = repository,
                    onLoginSuccess = {
                        currentMainTab = MainTab.BERANDA
                        currentScreen = Screen.Main(MainTab.BERANDA)
                    }
                )
            }

            is Screen.Main -> {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        ChoirBottomNavigation(
                            currentTab = currentMainTab,
                            onTabSelected = { tab ->
                                if (tab == MainTab.PRESENSI) {
                                    currentScreen = Screen.PresensiFlow
                                } else {
                                    currentMainTab = tab
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (currentMainTab) {
                            MainTab.BERANDA -> {
                                HomeScreen(
                                    user = currentUser,
                                    todayAttendance = filteredTodayRecords,
                                    posters = contentPosters,
                                    onOpenKotbahRaya = { currentScreen = Screen.KotbahRaya },
                                    onOpenKotbahPW = { currentScreen = Screen.KotbahPW },
                                    onOpenPesanGembala = { currentScreen = Screen.PesanGembala },
                                    onOpenMateri = { currentScreen = Screen.MateriPelayanan },
                                    onOpenJadwal = { currentScreen = Screen.Jadwal },
                                    onOpenRekapAbsen = { currentScreen = Screen.RekapAbsensi },
                                    onGoToPresensi = { currentScreen = Screen.PresensiFlow },
                                    onGoToBible = { currentMainTab = MainTab.BIBLE },
                                    onOpenAdmin = { currentScreen = Screen.AdminDashboard }
                                )
                            }
                            MainTab.BIBLE -> {
                                BibleScreen(
                                    db = db,
                                    userPrefs = userPrefs,
                                    onOpenChapter = { bookId, chapter ->
                                        currentScreen = Screen.BibleReader(bookId, chapter)
                                    },
                                    onOpenJournal = {
                                        currentScreen = Screen.RhemaJournal
                                    }
                                )
                            }
                            MainTab.PRESENSI -> {
                                currentScreen = Screen.PresensiFlow
                            }
                            MainTab.GALERI -> {
                                GalleryScreen(galleryItems = contentGallery)
                            }
                            MainTab.PROFIL -> {
                                ProfileScreen(
                                    user = currentUser,
                                    userPrefs = userPrefs,
                                    onLogout = {
                                        currentScreen = Screen.Login
                                    },
                                    onOpenAdmin = {
                                        currentScreen = Screen.AdminDashboard
                                    }
                                )
                            }
                        }
                    }
                }
            }

            is Screen.PresensiFlow -> {
                AttendanceScreen(
                    user = currentUser,
                    repository = repository,
                    onAttendanceSubmitted = { _: AttendanceRecord ->
                        currentScreen = Screen.RekapAbsensi
                    },
                    onCancel = {
                        currentScreen = Screen.Main(currentMainTab)
                    }
                )
            }

            is Screen.KotbahRaya -> {
                SermonListScreen(
                    category = SermonCategory.IBADAH_RAYA,
                    allSermons = contentSermons,
                    onBack = { currentScreen = Screen.Main(currentMainTab) }
                )
            }

            is Screen.KotbahPW -> {
                SermonListScreen(
                    category = SermonCategory.PW_PAGI,
                    allSermons = contentSermons,
                    onBack = { currentScreen = Screen.Main(currentMainTab) }
                )
            }

            is Screen.PesanGembala -> {
                SermonListScreen(
                    category = SermonCategory.PESAN_GEMBALA,
                    allSermons = contentSermons,
                    onBack = { currentScreen = Screen.Main(currentMainTab) }
                )
            }

            is Screen.MateriPelayanan -> {
                MateriPelayananScreen(
                    audioPlayer = audioPlayer,
                    allMateri = contentMateri,
                    onBack = { currentScreen = Screen.Main(currentMainTab) }
                )
            }

            is Screen.Jadwal -> {
                JadwalScreen(
                    jadwalUrl = contentJadwalUrl,
                    onBack = { currentScreen = Screen.Main(currentMainTab) }
                )
            }

            is Screen.RekapAbsensi -> {
                RekapAbsensiScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.Main(currentMainTab) }
                )
            }

            is Screen.BibleReader -> {
                BibleReaderScreen(
                    initialBookId = screen.bookId,
                    initialChapter = screen.chapter,
                    db = db,
                    userPrefs = userPrefs,
                    onBack = { currentScreen = Screen.Main(MainTab.BIBLE) }
                )
            }

            is Screen.RhemaJournal -> {
                RhemaJournalScreen(
                    db = db,
                    onBack = { currentScreen = Screen.Main(MainTab.BIBLE) }
                )
            }

            is Screen.AdminDashboard -> {
                AdminDashboardScreen(
                    contentRepo = contentRepo,
                    attendanceRepo = repository,
                    onBack = { currentScreen = Screen.Main(currentMainTab) }
                )
            }
        }
    }
}
