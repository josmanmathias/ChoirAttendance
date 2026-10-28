package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChoirAudioPlayer
import com.example.data.local.MediaDataProvider
import com.example.data.model.MateriItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MateriPelayananScreen(
    audioPlayer: ChoirAudioPlayer,
    allMateri: List<MateriItem> = MediaDataProvider.materiPelayanan,
    onBack: () -> Unit
) {
    BackHandler {
        audioPlayer.stop()
        onBack()
    }
    val context = LocalContext.current
    val voiceCategories = listOf("Semua", "Sopran", "Alto", "Tenor", "Bas")
    var selectedCategory by remember { mutableStateOf("Semua") }

    val filteredMateri = remember(selectedCategory, allMateri) {
        if (selectedCategory == "Semua") allMateri
        else allMateri.filter { it.voicePart == selectedCategory || it.voicePart == "Semua" }
    }

    val playbackState by audioPlayer.state.collectAsState()

    fun formatDuration(ms: Int): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%02d:%02d".format(min, sec)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Materi Pelayanan Choir",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        audioPlayer.stop()
                        onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChoirNavy)
            )
        },
        bottomBar = {
            // Built-in Audio Player Bar
            if (playbackState.currentTrackId != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = ChoirNavyDark,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MusicNote,
                                    contentDescription = null,
                                    tint = ChoirGoldLight,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = playbackState.currentTrackTitle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${formatDuration(playbackState.currentPositionMs)} / ${formatDuration(playbackState.totalDurationMs)}",
                                        fontSize = 11.sp,
                                        color = ChoirGoldLight
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (playbackState.isLoading) {
                                    CircularProgressIndicator(
                                        color = ChoirGoldLight,
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    IconButton(
                                        onClick = {
                                            if (playbackState.isPlaying) audioPlayer.pause()
                                            else audioPlayer.resume()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = "Play/Pause",
                                            tint = ChoirGoldLight,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                IconButton(onClick = { audioPlayer.stop() }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Tutup Player", tint = Color.White)
                                }
                            }
                        }

                        // Seekbar Slider
                        val maxVal = playbackState.totalDurationMs.toFloat().coerceAtLeast(1f)
                        val currVal = playbackState.currentPositionMs.toFloat().coerceIn(0f, maxVal)
                        Slider(
                            value = currVal,
                            onValueChange = { audioPlayer.seekTo(it.toInt()) },
                            valueRange = 0f..maxVal,
                            colors = SliderDefaults.colors(
                                thumbColor = ChoirGoldAmber,
                                activeTrackColor = ChoirGoldLight,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth().height(20.dp)
                        )
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
            // Voice Category Filter Chips
            ScrollableTabRow(
                selectedTabIndex = voiceCategories.indexOf(selectedCategory),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ChoirNavy
            ) {
                voiceCategories.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // List of Materi
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMateri, key = { it.id }) { item ->
                    MateriCardItem(
                        item = item,
                        isPlaying = playbackState.currentTrackId == item.id && playbackState.isPlaying,
                        onPlayAudio = {
                            if (item.audioUrl != null) {
                                audioPlayer.playTrack(item.id, item.title, item.audioUrl)
                            } else {
                                Toast.makeText(context, "Audio tidak tersedia", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onOpenPdf = {
                            if (item.pdfUrl != null) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.pdfUrl))
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Membuka partitur PDF: ${item.pdfUrl}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MateriCardItem(
    item: MateriItem,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onOpenPdf: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = when (item.voicePart) {
                        "Sopran" -> ChoirVoiceSoprano.copy(alpha = 0.15f)
                        "Alto" -> ChoirVoiceAlto.copy(alpha = 0.15f)
                        "Tenor" -> ChoirVoiceTenor.copy(alpha = 0.15f)
                        "Bas" -> ChoirVoiceBass.copy(alpha = 0.15f)
                        else -> ChoirRoyalBlue.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Bagian: ${item.voicePart}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.voicePart) {
                            "Sopran" -> ChoirVoiceSoprano
                            "Alto" -> ChoirVoiceAlto
                            "Tenor" -> ChoirVoiceTenor
                            "Bas" -> ChoirVoiceBass
                            else -> ChoirRoyalBlue
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "Latihan Vokal",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.lyrics != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.lyrics,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons (PDF & Audio Player)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PDF Button
                OutlinedButton(
                    onClick = onOpenPdf,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Partitur PDF", fontSize = 12.sp)
                }

                // Audio Button
                Button(
                    onClick = onPlayAudio,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) ChoirGoldAmber else ChoirNavy
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = if (isPlaying) ChoirNavyDark else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Jeda Audio" else "Putar Audio",
                        fontSize = 12.sp,
                        color = if (isPlaying) ChoirNavyDark else Color.White
                    )
                }
            }
        }
    }
}
