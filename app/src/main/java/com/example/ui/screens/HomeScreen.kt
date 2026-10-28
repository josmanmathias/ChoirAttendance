package com.example.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.MediaDataProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceType
import com.example.data.model.User
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    user: User?,
    todayAttendance: List<AttendanceRecord>,
    posters: List<com.example.data.model.PosterItem> = MediaDataProvider.sliderPosters,
    onOpenKotbahRaya: () -> Unit,
    onOpenKotbahPW: () -> Unit,
    onOpenPesanGembala: () -> Unit,
    onOpenMateri: () -> Unit,
    onOpenJadwal: () -> Unit,
    onOpenRekapAbsen: () -> Unit,
    onGoToPresensi: () -> Unit,
    onGoToBible: () -> Unit,
    onOpenAdmin: (() -> Unit)? = null
) {
    // Dynamic greeting based on current time
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    val pagerState = rememberPagerState(pageCount = { posters.size })

    // Auto-scroll slider
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            if (pagerState.pageCount > 0) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage, animationSpec = tween(600))
            }
        }
    }

    val hasAttendedToday = todayAttendance.isNotEmpty()
    val latihanRecord = todayAttendance.find { it.type == AttendanceType.LATIHAN }
    val ibadahRecord = todayAttendance.find { it.type == AttendanceType.IBADAH }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // --- 1. Top Header & User Greeting ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ChoirNavyDark, ChoirNavy)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, ChoirGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_choir),
                            contentDescription = "Avatar Choir",
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$greeting,",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.WbSunny,
                                contentDescription = null,
                                tint = ChoirGoldLight,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Text(
                            text = user?.namaLengkap ?: "Anggota Choir",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Surface(
                                color = when (user?.posisi?.lowercase()) {
                                    "sopran" -> ChoirVoiceSoprano.copy(alpha = 0.25f)
                                    "alto" -> ChoirVoiceAlto.copy(alpha = 0.25f)
                                    "tenor" -> ChoirVoiceTenor.copy(alpha = 0.25f)
                                    "bas" -> ChoirVoiceBass.copy(alpha = 0.25f)
                                    else -> ChoirGold.copy(alpha = 0.25f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Vokal: ${user?.posisi ?: "Sopran"}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChoirGoldLight,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "JKI Ecclesia",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onOpenAdmin != null && (user?.role == "admin" || user?.username == "josman")) {
                        FilledTonalButton(
                            onClick = onOpenAdmin,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ChoirGoldAmber,
                                contentColor = ChoirNavyDark
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = ChoirNavyDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Admin",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onGoToBible,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifikasi",
                            tint = ChoirGoldLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. Ringkasan Data Absensi Hari Ini (Compact) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ringkasan Absensi Hari Ini",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        color = if (hasAttendedToday) SuccessGreen.copy(alpha = 0.15f) else WarningOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (hasAttendedToday) "Sudah Hadir" else "Belum Hadir",
                            color = if (hasAttendedToday) SuccessGreen else WarningOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Latihan
                    StatusAttendanceChip(
                        title = "Latihan",
                        record = latihanRecord,
                        modifier = Modifier.weight(1f)
                    )

                    // Status Ibadah
                    StatusAttendanceChip(
                        title = "Ibadah",
                        record = ibadahRecord,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (!hasAttendedToday) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onGoToPresensi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ChoirGoldAmber)
                    ) {
                        Text(
                            text = "Buka Presensi Sekarang",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChoirNavyDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. Carousel Poster Slider ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Informasi & Pengumuman Choir",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${pagerState.currentPage + 1}/${posters.size}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 12.dp
            ) { page ->
                val poster = posters[page]
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(4.dp, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = poster.imageUrl,
                            contentDescription = poster.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = poster.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = poster.subtitle,
                                color = ChoirGoldLight,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Indicator Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(posters.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) ChoirGold else ChoirCardBorder
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(if (pagerState.currentPage == iteration) 8.dp else 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 4. 6 Card Menu (2 Rows x 3 Columns) ---
        Text(
            text = "Menu Pelayanan Choir",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: Kotbah Ibadah Raya, Kotbah PW Pagi, Pesan Gembala
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MenuFeatureCard(
                    title = "Kotbah\nIbadah Raya",
                    icon = Icons.Filled.PlayCircle,
                    accentColor = Color(0xFFEF4444),
                    onClick = onOpenKotbahRaya,
                    modifier = Modifier.weight(1f)
                )

                MenuFeatureCard(
                    title = "Kotbah\nPW Pagi",
                    icon = Icons.Filled.WbSunny,
                    accentColor = Color(0xFFF59E0B),
                    onClick = onOpenKotbahPW,
                    modifier = Modifier.weight(1f)
                )

                MenuFeatureCard(
                    title = "Pesan\nGembala",
                    icon = Icons.Filled.RecordVoiceOver,
                    accentColor = ChoirRoyalBlue,
                    onClick = onOpenPesanGembala,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: Materi Pelayanan, Jadwal, Rekap Absensi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MenuFeatureCard(
                    title = "Materi\nPelayanan",
                    icon = Icons.Filled.LibraryMusic,
                    accentColor = Color(0xFF8B5CF6),
                    onClick = onOpenMateri,
                    modifier = Modifier.weight(1f)
                )

                MenuFeatureCard(
                    title = "Jadwal\nChoir",
                    icon = Icons.Filled.CalendarMonth,
                    accentColor = Color(0xFF10B981),
                    onClick = onOpenJadwal,
                    modifier = Modifier.weight(1f)
                )

                MenuFeatureCard(
                    title = "Rekap\nAbsensi",
                    icon = Icons.Filled.Assessment,
                    accentColor = ChoirNavy,
                    onClick = onOpenRekapAbsen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusAttendanceChip(
    title: String,
    record: AttendanceRecord?,
    modifier: Modifier = Modifier
) {
    val isPresent = record != null
    Surface(
        modifier = modifier,
        color = if (isPresent) SuccessGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPresent) SuccessGreen.copy(alpha = 0.35f) else ChoirCardBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPresent) Icons.Filled.CheckCircle else Icons.Filled.Schedule,
                contentDescription = null,
                tint = if (isPresent) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isPresent) "Pukul ${record.waktu.take(5)}" else "Belum hadir",
                    fontSize = 9.sp,
                    color = if (isPresent) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MenuFeatureCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.95f)
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("card_menu_${title.replace("\n", "_").lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}
