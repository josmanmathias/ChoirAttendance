package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatColorReset
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.BibleDataProvider
import com.example.data.local.CompletedChapterEntity
import com.example.data.local.RhemaNoteEntity
import com.example.data.local.UserPreferences
import com.example.data.local.VerseHighlightEntity
import com.example.data.model.BibleBook
import com.example.data.model.BibleVerse
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleReaderScreen(
    initialBookId: Int,
    initialChapter: Int,
    db: AppDatabase,
    userPrefs: UserPreferences,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var currentBookId by remember { mutableIntStateOf(initialBookId) }
    var currentChapter by remember { mutableIntStateOf(initialChapter) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val book: BibleBook = remember(currentBookId) {
        BibleDataProvider.getBookById(currentBookId) ?: BibleBook(currentBookId, "Kitab", "PL", 1)
    }

    val verses: List<BibleVerse> = remember(currentBookId, currentChapter) {
        BibleDataProvider.getVersesForChapter(currentBookId, currentChapter)
    }

    val todayDateStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Completed status
    val completedFlow = remember(currentBookId, currentChapter, todayDateStr) {
        db.bibleDao().getCompletedChaptersByDate(todayDateStr)
    }
    val completedList by completedFlow.collectAsState(initial = emptyList())
    val isCompleted = remember(completedList, currentBookId, currentChapter) {
        completedList.any { it.bookId == currentBookId && it.chapter == currentChapter }
    }

    // Highlights
    val highlightsFlow = remember(currentBookId, currentChapter) {
        db.bibleDao().getHighlights(currentBookId, currentChapter)
    }
    val highlights by highlightsFlow.collectAsState(initial = emptyList())
    val highlightMap = remember(highlights) {
        highlights.associateBy({ it.verse }, { Color(android.graphics.Color.parseColor(it.colorHex)) })
    }

    // 2-Minute reading timer (120 seconds)
    var readingSeconds by remember(currentBookId, currentChapter) { mutableIntStateOf(0) }
    var autoMarkedCelebration by remember { mutableStateOf(false) }

    fun markCurrentChapterCompleted() {
        coroutineScope.launch {
            db.bibleDao().markChapterCompleted(
                CompletedChapterEntity(
                    date = todayDateStr,
                    bookId = currentBookId,
                    chapter = currentChapter
                )
            )
            // Check if all 5 completed
            val count = db.bibleDao().getCompletedCountForDate(todayDateStr)
            if (count >= 5) {
                userPrefs.recordDailyStrike(todayDateStr)
            } else {
                userPrefs.addPoints(1)
            }
            Toast.makeText(context, "${book.name} $currentChapter ditandai Selesai Dibaca!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(currentBookId, currentChapter, isCompleted) {
        if (!isCompleted) {
            readingSeconds = 0
            while (readingSeconds < 120) {
                delay(1000)
                readingSeconds++
                if (readingSeconds >= 120) {
                    markCurrentChapterCompleted()
                    autoMarkedCelebration = true
                    delay(3000)
                    autoMarkedCelebration = false
                }
            }
        }
    }

    // Selected verse for action dialog
    var selectedVerse by remember { mutableStateOf<BibleVerse?>(null) }
    var showRhemaDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${book.name} $currentChapter",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = if (isCompleted) "✓ Sudah Selesai Dibaca" else "Waktu baca: ${readingSeconds}s / 120s (Otomatis)",
                            fontSize = 11.sp,
                            color = if (isCompleted) SuccessGreen else ChoirGoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    if (!isCompleted) {
                        TextButton(
                            onClick = { markCurrentChapterCompleted() },
                            colors = ButtonDefaults.textButtonColors(contentColor = ChoirGoldLight)
                        ) {
                            Text("Tandai Selesai", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Selesai",
                            tint = SuccessGreen,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ChoirNavy,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Chapter Navigation Footer
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Chapter
                    OutlinedButton(
                        onClick = {
                            if (currentChapter > 1) {
                                currentChapter--
                            } else if (currentBookId > 1) {
                                currentBookId--
                                val prevBook = BibleDataProvider.getBookById(currentBookId)!!
                                currentChapter = prevBook.chapterCount
                            }
                        },
                        enabled = currentBookId > 1 || currentChapter > 1,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pasal Sblm", fontSize = 11.sp)
                    }

                    // Timer progress bar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                    ) {
                        val progress = if (isCompleted) 1f else (readingSeconds / 120f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = if (isCompleted) SuccessGreen else ChoirGoldAmber,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isCompleted) "Tuntas (2 Menit)" else "Baca 2 min: ${120 - readingSeconds}s tersisa",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Next Chapter
                    OutlinedButton(
                        onClick = {
                            if (currentChapter < book.chapterCount) {
                                currentChapter++
                            } else if (currentBookId < 66) {
                                currentBookId++
                                currentChapter = 1
                            }
                        },
                        enabled = currentBookId < 66 || currentChapter < book.chapterCount,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Pasal Brkt", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Auto-mark Celebration banner
            AnimatedVisibility(visible = autoMarkedCelebration) {
                Surface(
                    color = SuccessGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hebat! Anda telah membaca selama 2 menit. Pasal ditandai selesai!",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Verses List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "${book.name} - Pasal $currentChapter (Terjemahan Baru)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "Ketuk ayat mana saja untuk memberi highlight warna, salin ayat, atau tulis rhema catatan.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                }

                items(verses, key = { it.verse }) { verseItem ->
                    val highlightColor = highlightMap[verseItem.verse]
                    VerseRowItem(
                        verse = verseItem,
                        highlightColor = highlightColor,
                        onClick = { selectedVerse = verseItem }
                    )
                }
            }
        }
    }

    // Modal Sheet for Verse Actions (Highlight, Copy, Rhema)
    if (selectedVerse != null) {
        val v = selectedVerse!!
        AlertDialog(
            onDismissRequest = { selectedVerse = null },
            title = {
                Text(
                    text = "${book.name} $currentChapter:${v.verse}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "\"${v.text}\"",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Beri Warna Highlight:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Gold
                        HighlightColorCircle(color = Color(0xFFFEF08A)) {
                            coroutineScope.launch {
                                db.bibleDao().saveHighlight(
                                    VerseHighlightEntity(currentBookId, currentChapter, v.verse, "#FEF08A")
                                )
                                selectedVerse = null
                            }
                        }
                        // Green
                        HighlightColorCircle(color = Color(0xFFBBF7D0)) {
                            coroutineScope.launch {
                                db.bibleDao().saveHighlight(
                                    VerseHighlightEntity(currentBookId, currentChapter, v.verse, "#BBF7D0")
                                )
                                selectedVerse = null
                            }
                        }
                        // Blue
                        HighlightColorCircle(color = Color(0xFFBAE6FD)) {
                            coroutineScope.launch {
                                db.bibleDao().saveHighlight(
                                    VerseHighlightEntity(currentBookId, currentChapter, v.verse, "#BAE6FD")
                                )
                                selectedVerse = null
                            }
                        }
                        // Pink
                        HighlightColorCircle(color = Color(0xFFFBCFE8)) {
                            coroutineScope.launch {
                                db.bibleDao().saveHighlight(
                                    VerseHighlightEntity(currentBookId, currentChapter, v.verse, "#FBCFE8")
                                )
                                selectedVerse = null
                            }
                        }
                        // Clear highlight
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    db.bibleDao().deleteHighlight(
                                        VerseHighlightEntity(currentBookId, currentChapter, v.verse, "")
                                    )
                                    selectedVerse = null
                                }
                            }
                        ) {
                            Icon(Icons.Filled.FormatColorReset, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons: Copy & Rhema
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Ayat Alkitab", "${book.name} $currentChapter:${v.verse} - ${v.text}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Ayat disalin ke clipboard", Toast.LENGTH_SHORT).show()
                                selectedVerse = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                showRhemaDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                        ) {
                            Icon(Icons.Filled.EditNote, contentDescription = null, tint = ChoirGoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rhema", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedVerse = null }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Dialog Tulis Rhema / Catatan
    if (showRhemaDialog && selectedVerse != null) {
        val v = selectedVerse!!
        var rhemaTitle by remember { mutableStateOf("Refleksi ${book.name} $currentChapter:${v.verse}") }
        var rhemaContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRhemaDialog = false },
            title = {
                Text("Tulis Catatan / Rhema", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Ayat: ${book.name} $currentChapter:${v.verse}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChoirGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rhemaTitle,
                        onValueChange = { rhemaTitle = it },
                        label = { Text("Judul Rhema") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rhemaContent,
                        onValueChange = { rhemaContent = it },
                        label = { Text("Isi Renungan / Catatan Pribadi...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rhemaContent.isNotBlank()) {
                            coroutineScope.launch {
                                db.bibleDao().saveRhemaNote(
                                    RhemaNoteEntity(
                                        id = UUID.randomUUID().toString(),
                                        date = todayDateStr,
                                        bookId = currentBookId,
                                        chapter = currentChapter,
                                        reference = "${book.name} $currentChapter:${v.verse}",
                                        title = rhemaTitle,
                                        content = rhemaContent
                                    )
                                )
                                Toast.makeText(context, "Rhema berhasil disimpan di jurnal!", Toast.LENGTH_SHORT).show()
                                showRhemaDialog = false
                                selectedVerse = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Simpan Rhema", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRhemaDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun VerseRowItem(
    verse: BibleVerse,
    highlightColor: Color?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(highlightColor ?: Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "${verse.verse}. ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ChoirGold,
            modifier = Modifier.width(28.dp)
        )
        Text(
            text = verse.text,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HighlightColorCircle(
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, Color.Gray.copy(alpha = 0.4f), CircleShape)
            .clickable(onClick = onClick)
    )
}
