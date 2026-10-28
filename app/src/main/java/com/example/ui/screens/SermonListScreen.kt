package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MediaDataProvider
import com.example.data.model.SermonCategory
import com.example.data.model.SermonItem
import com.example.ui.theme.ChoirNavy
import com.example.ui.theme.ChoirNavyDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SermonListScreen(
    category: SermonCategory,
    allSermons: List<SermonItem> = MediaDataProvider.sermons,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val sermons = remember(category, allSermons) {
        allSermons.filter { it.category == category }
    }

    var selectedSermonToPlay by remember { mutableStateOf<SermonItem?>(null) }

    fun playSermon(sermon: SermonItem) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${sermon.youtubeId}"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${sermon.youtubeId}"))
        try {
            context.startActivity(appIntent)
        } catch (_: Exception) {
            try {
                context.startActivity(webIntent)
            } catch (_: Exception) {
                selectedSermonToPlay = sermon
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category.label,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChoirNavy)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(sermons, key = { it.id }) { sermon ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(14.dp))
                        .clickable { playSermon(sermon) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Thumbnail with Play button overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = "https://img.youtube.com/vi/${sermon.youtubeId}/hqdefault.jpg",
                                contentDescription = sermon.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .align(Alignment.Center)
                                    .shadow(6.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Putar",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Surface(
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = sermon.duration,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Info
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = sermon.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = sermon.preacher,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = sermon.date,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedSermonToPlay != null) {
        val s = selectedSermonToPlay!!
        AlertDialog(
            onDismissRequest = { selectedSermonToPlay = null },
            title = { Text(s.title, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Text("Video YouTube dapat diputar di browser atau aplikasi YouTube:\nhttps://youtu.be/${s.youtubeId}")
            },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/${s.youtubeId}"))
                    context.startActivity(intent)
                    selectedSermonToPlay = null
                }) {
                    Text("Buka YouTube")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSermonToPlay = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}
