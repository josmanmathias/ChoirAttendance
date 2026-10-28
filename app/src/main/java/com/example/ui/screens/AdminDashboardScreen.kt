package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AttendanceRecord
import com.example.data.model.SermonCategory
import com.example.data.model.User
import com.example.data.repository.AttendanceRepository
import com.example.data.repository.ContentRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    contentRepo: ContentRepository,
    attendanceRepo: AttendanceRepository,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val posters by contentRepo.posters.collectAsState()
    val sermons by contentRepo.sermons.collectAsState()
    val materi by contentRepo.materi.collectAsState()
    val gallery by contentRepo.gallery.collectAsState()
    val jadwalUrl by contentRepo.jadwalUrl.collectAsState()
    val members by contentRepo.members.collectAsState()

    val attendanceFlow = remember { attendanceRepo.getLocalAttendanceHistory() }
    val attendanceRecords by attendanceFlow.collectAsState(initial = emptyList())

    val adminTabs = listOf("Slider", "Kotbah", "Materi SATB", "Jadwal & Galeri", "Anggota")
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog state controllers
    var showAddPosterDialog by remember { mutableStateOf(false) }
    var showAddSermonDialog by remember { mutableStateOf(false) }
    var showAddMateriDialog by remember { mutableStateOf(false) }
    var showAddGalleryDialog by remember { mutableStateOf(false) }
    var showEditJadwalDialog by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = ChoirGoldLight, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Panel Admin Sion Choir",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Kelola Konten & Data Anggota JKI Ecclesia",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Metrics Bar
            Surface(
                color = ChoirNavyDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricMiniItem("Poster", "${posters.size}")
                    MetricMiniItem("Kotbah", "${sermons.size}")
                    MetricMiniItem("Materi", "${materi.size}")
                    MetricMiniItem("Anggota", "${members.size}")
                    MetricMiniItem("Absensi", "${attendanceRecords.size}")
                }
            }

            // Tab Navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ChoirNavy
            ) {
                adminTabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tabTitle,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            // Content per Tab
            when (selectedTab) {
                0 -> {
                    // TAB 1: KELOLA POSTER / SLIDER
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Poster Carousel (${posters.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Button(
                                onClick = { showAddPosterDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = ChoirGoldLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Poster", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(posters, key = { it.id }) { p ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ChoirCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = p.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(p.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                            Text(p.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                        }
                                        IconButton(onClick = {
                                            contentRepo.deletePoster(p.id)
                                            Toast.makeText(context, "Poster dihapus", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.8f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 2: KELOLA KOTBAH & PESAN GEMBALA
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Kotbah YouTube (${sermons.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Button(
                                onClick = { showAddSermonDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = ChoirGoldLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Kotbah", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(sermons, key = { it.id }) { s ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ChoirCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = "https://img.youtube.com/vi/${s.youtubeId}/hqdefault.jpg",
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(s.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                            Text("${s.category.label} • ${s.date}", fontSize = 11.sp, color = ChoirRoyalBlue)
                                            Text("Pembicara: ${s.preacher}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        IconButton(onClick = {
                                            contentRepo.deleteSermon(s.id)
                                            Toast.makeText(context, "Kotbah dihapus", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.8f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 3: KELOLA MATERI PELAYANAN (SATB)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Partitur & Audio (${materi.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Button(
                                onClick = { showAddMateriDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = ChoirGoldLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Materi", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(materi, key = { it.id }) { m ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ChoirCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = ChoirNavy.copy(alpha = 0.1f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Filled.LibraryMusic, contentDescription = null, tint = ChoirNavy, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(m.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                            Text("Vokal: ${m.voicePart} • ${m.subtitle}", fontSize = 11.sp, color = ChoirRoyalBlue, maxLines = 1)
                                            Text(
                                                text = "PDF: ${m.pdfUrl != null} | Audio: ${m.audioUrl != null}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = {
                                            contentRepo.deleteMateri(m.id)
                                            Toast.makeText(context, "Materi dihapus", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.8f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 4: KELOLA JADWAL & GALERI
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Section 1: Jadwal URL
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ChoirCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("File Jadwal Pelayanan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    OutlinedButton(
                                        onClick = { showEditJadwalDialog = true },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Ubah Link URL", fontSize = 10.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(jadwalUrl, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Section 2: Galeri
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Foto Dokumentasi Galeri (${gallery.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Button(
                                onClick = { showAddGalleryDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = ChoirGoldLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Foto", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(gallery, key = { it.id }) { g ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ChoirCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = g.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(g.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                            Text(g.date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        IconButton(onClick = {
                                            contentRepo.deleteGalleryItem(g.id)
                                            Toast.makeText(context, "Foto dihapus", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.8f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // TAB 5: KELOLA ANGGOTA & MONITORING PRESENSI
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Anggota Choir (Tabel chsn_users)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Button(
                                onClick = { showAddMemberDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = ChoirGoldLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Anggota", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(members, key = { it.id }) { u ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ChoirCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = when (u.posisi) {
                                                        "Sopran" -> ChoirVoiceSoprano.copy(alpha = 0.15f)
                                                        "Alto" -> ChoirVoiceAlto.copy(alpha = 0.15f)
                                                        "Tenor" -> ChoirVoiceTenor.copy(alpha = 0.15f)
                                                        else -> ChoirVoiceBass.copy(alpha = 0.15f)
                                                    },
                                                    shape = CircleShape,
                                                    modifier = Modifier.size(34.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(u.posisi.take(1), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(u.namaLengkap, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("@${u.username} • ${u.noHp ?: "-"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }

                                            Surface(
                                                color = if (u.role == "admin") ChoirGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = u.role.uppercase(),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (u.role == "admin") ChoirGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Role & Vokal Quick Change
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf("Sopran", "Alto", "Tenor", "Bas").forEach { pos ->
                                                FilterChip(
                                                    selected = u.posisi == pos,
                                                    onClick = {
                                                        contentRepo.updateMember(u.id, pos, u.role, u.status)
                                                        Toast.makeText(context, "${u.namaLengkap} diubah ke $pos", Toast.LENGTH_SHORT).show()
                                                    },
                                                    label = { Text(pos, fontSize = 9.sp) },
                                                    modifier = Modifier.height(28.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---

    // 1. Add Poster Dialog
    if (showAddPosterDialog) {
        var title by remember { mutableStateOf("") }
        var subtitle by remember { mutableStateOf("") }
        var imageUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPosterDialog = false },
            title = { Text("Tambah Poster Slider Carousel", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Poster") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Sub-judul / Waktu") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL Gambar (https://...)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            contentRepo.addPoster(title, subtitle, imageUrl)
                            showAddPosterDialog = false
                            Toast.makeText(context, "Poster carousel berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPosterDialog = false }) { Text("Batal") }
            }
        )
    }

    // 2. Add Sermon Dialog
    if (showAddSermonDialog) {
        var title by remember { mutableStateOf("") }
        var preacher by remember { mutableStateOf("Pdt. JKI Anugrah Ecclesia") }
        var date by remember { mutableStateOf("Minggu Ini") }
        var duration by remember { mutableStateOf("45:00") }
        var youtubeInput by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(SermonCategory.IBADAH_RAYA) }

        AlertDialog(
            onDismissRequest = { showAddSermonDialog = false },
            title = { Text("Tambah Video Kotbah", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Kotbah") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = preacher, onValueChange = { preacher = it }, label = { Text("Pembicara / Pengkhotbah") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = youtubeInput, onValueChange = { youtubeInput = it }, label = { Text("YouTube URL atau Video ID (cth: dQw4w9WgXcQ)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(selected = category == SermonCategory.IBADAH_RAYA, onClick = { category = SermonCategory.IBADAH_RAYA }, label = { Text("Ibadah Raya", fontSize = 9.sp) })
                        FilterChip(selected = category == SermonCategory.PW_PAGI, onClick = { category = SermonCategory.PW_PAGI }, label = { Text("PW Pagi", fontSize = 9.sp) })
                        FilterChip(selected = category == SermonCategory.PESAN_GEMBALA, onClick = { category = SermonCategory.PESAN_GEMBALA }, label = { Text("Pesan Gembala", fontSize = 9.sp) })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            contentRepo.addSermon(title, preacher, date, duration, youtubeInput, category)
                            showAddSermonDialog = false
                            Toast.makeText(context, "Kotbah berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSermonDialog = false }) { Text("Batal") }
            }
        )
    }

    // 3. Add Materi Dialog
    if (showAddMateriDialog) {
        var title by remember { mutableStateOf("") }
        var subtitle by remember { mutableStateOf("Partitur SATB Paduan Suara") }
        var voicePart by remember { mutableStateOf("Semua") }
        var pdfUrl by remember { mutableStateOf("https://terbangcirebon.sch.id/jki/ministry/choir/materi/sample.pdf") }
        var audioUrl by remember { mutableStateOf("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3") }
        var lyrics by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMateriDialog = false },
            title = { Text("Tambah Materi Pelayanan (SATB)", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Lagu") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Keterangan Aransemen") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Semua", "Sopran", "Alto", "Tenor", "Bas").forEach { part ->
                            FilterChip(selected = voicePart == part, onClick = { voicePart = part }, label = { Text(part, fontSize = 9.sp) })
                        }
                    }
                    OutlinedTextField(value = pdfUrl, onValueChange = { pdfUrl = it }, label = { Text("URL Partitur PDF") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = audioUrl, onValueChange = { audioUrl = it }, label = { Text("URL File Audio Latihan") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = lyrics, onValueChange = { lyrics = it }, label = { Text("Catatan / Panduan Lirik") }, modifier = Modifier.fillMaxWidth().height(70.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            contentRepo.addMateri(title, subtitle, voicePart, pdfUrl, audioUrl, lyrics.ifBlank { null })
                            showAddMateriDialog = false
                            Toast.makeText(context, "Materi latihan berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMateriDialog = false }) { Text("Batal") }
            }
        )
    }

    // 4. Edit Jadwal URL Dialog
    if (showEditJadwalDialog) {
        var inputUrl by remember { mutableStateOf(jadwalUrl) }

        AlertDialog(
            onDismissRequest = { showEditJadwalDialog = false },
            title = { Text("Ubah Link File Jadwal", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Masukkan URL gambar jadwal pelayanan choir yang baru (PNG/JPG):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = inputUrl, onValueChange = { inputUrl = it }, label = { Text("URL Jadwal") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        contentRepo.updateJadwalUrl(inputUrl)
                        showEditJadwalDialog = false
                        Toast.makeText(context, "URL Jadwal diperbarui!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Perbarui", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditJadwalDialog = false }) { Text("Batal") }
            }
        )
    }

    // 5. Add Gallery Dialog
    if (showAddGalleryDialog) {
        var title by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("Oktober 2026") }
        var imageUrl by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddGalleryDialog = false },
            title = { Text("Tambah Foto Galeri", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Kegiatan") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Tanggal / Bulan") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL Gambar Foto") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Keterangan Dokumentasi") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            contentRepo.addGalleryItem(title, date, imageUrl, description)
                            showAddGalleryDialog = false
                            Toast.makeText(context, "Foto galeri ditambahkan!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGalleryDialog = false }) { Text("Batal") }
            }
        )
    }

    // 6. Add Member Dialog
    if (showAddMemberDialog) {
        var nama by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var posisi by remember { mutableStateOf("Sopran") }
        var role by remember { mutableStateOf("member") }
        var noHp by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMemberDialog = false },
            title = { Text("Tambah Anggota Choir (chsn_users)", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Lengkap") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username Login") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = noHp, onValueChange = { noHp = it }, label = { Text("No. WhatsApp / HP") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Text("Bagian Vokal:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Sopran", "Alto", "Tenor", "Bas").forEach { part ->
                            FilterChip(selected = posisi == part, onClick = { posisi = part }, label = { Text(part, fontSize = 9.sp) })
                        }
                    }
                    Text("Role Akses:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = role == "member", onClick = { role = "member" }, label = { Text("Member", fontSize = 10.sp) })
                        FilterChip(selected = role == "admin", onClick = { role = "admin" }, label = { Text("Admin", fontSize = 10.sp) })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nama.isNotBlank() && username.isNotBlank()) {
                            contentRepo.addMember(nama, username, posisi, role, noHp)
                            showAddMemberDialog = false
                            Toast.makeText(context, "Anggota choir berhasil didaftarkan!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Daftarkan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMemberDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun MetricMiniItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ChoirGoldLight)
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
    }
}
