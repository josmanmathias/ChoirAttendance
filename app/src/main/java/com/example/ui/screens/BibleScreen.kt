package com.example.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.BibleDataProvider
import com.example.data.local.UserPreferences
import com.example.data.model.BibleVerse
import com.example.data.model.DailyReadingPlan
import com.example.data.model.ReadingChapterTarget
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BibleScreen(
    db: AppDatabase,
    userPrefs: UserPreferences,
    onOpenChapter: (bookId: Int, chapter: Int) -> Unit,
    onOpenJournal: () -> Unit
) {
    var currentDate by remember { mutableStateOf(Date()) }

    val cal = Calendar.getInstance().apply { time = currentDate }
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val todayDateStr = remember(currentDate) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)
    }

    val plan: DailyReadingPlan = remember(currentDate) {
        BibleDataProvider.getPlanForDate(currentDate)
    }

    val completedChaptersFlow = remember(todayDateStr) {
        db.bibleDao().getCompletedChaptersByDate(todayDateStr)
    }
    val completedEntities by completedChaptersFlow.collectAsState(initial = emptyList())
    val completedSet = remember(completedEntities) {
        completedEntities.map { "${it.bookId}_${it.chapter}" }.toSet()
    }

    val biblePoints by userPrefs.biblePoints.collectAsState(initial = 15)
    val dailyStreak by userPrefs.dailyStreak.collectAsState(initial = 3)
    val weeklyStreaks by userPrefs.weeklyStreaks.collectAsState(initial = 1)
    val monthlyStreaks by userPrefs.monthlyStreaks.collectAsState(initial = 0)

    val randomAmsalVerses = remember(dayOfMonth) {
        BibleDataProvider.getRandomAmsalHighlights(dayOfMonth)
    }
    val pagerState = rememberPagerState(pageCount = { randomAmsalVerses.size })

    // Auto rotate amsal verses
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            if (pagerState.pageCount > 0) {
                val next = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(next, animationSpec = tween(500))
            }
        }
    }

    val completedCount = plan.chapters.count { completedSet.contains("${it.bookId}_${it.chapter}") }
    val isAllCompletedToday = completedCount == plan.chapters.size && plan.chapters.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
            .testTag("bible_screen")
    ) {
        // --- Header Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ChoirNavyDark, ChoirNavy)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "BACA FIRMAN TIAP HARI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = ChoirGoldLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "1 Januari – 31 Desember • 5 Pasal / Hari",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Points pill
                    Surface(
                        color = ChoirGold.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ChoirGoldLight),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Poin",
                                tint = ChoirGoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$biblePoints Pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChoirGoldLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Streak Badges (Harian, Mingguan, Bulanan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StreakBadgeItem(
                        title = "STRIKE HARIAN",
                        value = "$dailyStreak Hari",
                        icon = Icons.Filled.LocalFireDepartment,
                        color = Color(0xFFF97316),
                        isActive = dailyStreak > 0,
                        modifier = Modifier.weight(1f)
                    )
                    StreakBadgeItem(
                        title = "MINGGUAN",
                        value = "$weeklyStreaks Minggu",
                        icon = Icons.Filled.WorkspacePremium,
                        color = ChoirGold,
                        isActive = weeklyStreaks > 0,
                        modifier = Modifier.weight(1f)
                    )
                    StreakBadgeItem(
                        title = "BULANAN",
                        value = "$monthlyStreaks Bulan",
                        icon = Icons.Filled.EmojiEvents,
                        color = ChoirRoyalBlue,
                        isActive = monthlyStreaks > 0,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Random Amsal Ayat Hari Ini Carousel ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(3.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = ChoirGoldSurface,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("A", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ChoirGold)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ayat Hari Ini (Amsal Pasal $dayOfMonth)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Amsal Sesuai Tanggal",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                ) { page ->
                    val v = randomAmsalVerses[page]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "\"${v.text}\"",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Amsal ${v.chapter}:${v.verse} (TB)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChoirGold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Date Navigation ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    val prevCal = Calendar.getInstance().apply {
                        time = currentDate
                        add(Calendar.DAY_OF_YEAR, -1)
                    }
                    currentDate = prevCal.time
                }
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Hari Sebelumnya")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = plan.dateDisplay,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = {
                    val nextCal = Calendar.getInstance().apply {
                        time = currentDate
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                    currentDate = nextCal.time
                }
            ) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Hari Berikutnya")
            }
        }

        // Today Button & Progress Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progress: $completedCount/5 Pasal Selesai",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isAllCompletedToday) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = { currentDate = Date() },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("Ke Hari Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- Daftar 5 Pembacaan Hari Ini ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            plan.chapters.forEachIndexed { index, target ->
                val isDone = completedSet.contains("${target.bookId}_${target.chapter}")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clickable { onOpenChapter(target.bookId, target.chapter) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDone) SuccessGreen.copy(alpha = 0.4f) else ChoirCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDone) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDone) SuccessGreen else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "${target.bookName} ${target.chapter}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = target.category,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Status Icon
                        if (isDone) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Selesai",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Sudah Dibaca",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Baca",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = "Belum Dibaca",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rhema & Reflection Button
        Button(
            onClick = onOpenJournal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
        ) {
            Icon(Icons.Filled.EditNote, contentDescription = null, tint = ChoirGoldLight)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Catatan & Jurnal Rhema Saya",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StreakBadgeItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = if (isActive) color.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) color.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) color else Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f)
            )
        }
    }
}
