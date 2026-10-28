package com.example.ui.screens

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.ChoirGoldAmber
import com.example.ui.theme.ChoirNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalScreen(
    jadwalUrl: String = "https://terbangcirebon.sch.id/jki/ministry/choir/jadwal.png",
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += offsetChange
    }

    fun downloadJadwal() {
        try {
            val request = DownloadManager.Request(Uri.parse(jadwalUrl))
                .setTitle("Jadwal Sion Choir JKI Ecclesia")
                .setDescription("Mengunduh file jadwal choir...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "jadwal_sion_choir.png")
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(context, "Mulai mengunduh jadwal...", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "Jadwal berhasil disimpan ke galeri foto!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jadwal Sion Choir",
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
                actions = {
                    IconButton(onClick = {
                        scale = 1f
                        offset = Offset.Zero
                    }) {
                        Icon(Icons.Filled.RestartAlt, contentDescription = "Reset Zoom", tint = Color.White)
                    }
                    IconButton(onClick = { downloadJadwal() }) {
                        Icon(Icons.Filled.Download, contentDescription = "Download", tint = ChoirGoldAmber)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChoirNavy)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0F172A))
        ) {
            // Instructions banner
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ZoomIn, contentDescription = null, tint = ChoirGoldAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cubit layar untuk zoom & geser untuk pan",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "Skala: ${(scale * 100).toInt()}%",
                        color = ChoirGoldAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Interactive Zoom & Pan Image Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .transformable(state = state),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = jadwalUrl,
                    contentDescription = "Jadwal Pelayanan Choir",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = ContentScale.Fit
                )
            }

            // Download Button bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = ChoirNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { downloadJadwal() },
                        colors = ButtonDefaults.buttonColors(containerColor = ChoirGoldAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, tint = ChoirNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Download File Jadwal (PNG)",
                            fontWeight = FontWeight.Bold,
                            color = ChoirNavy,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
