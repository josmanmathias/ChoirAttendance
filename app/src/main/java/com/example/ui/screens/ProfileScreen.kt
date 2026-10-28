package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.UserPreferences
import com.example.data.model.User
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    user: User?,
    userPrefs: UserPreferences,
    onLogout: () -> Unit,
    onOpenAdmin: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val biblePoints by userPrefs.biblePoints.collectAsState(initial = 15)
    val dailyStreak by userPrefs.dailyStreak.collectAsState(initial = 3)
    val weeklyStreaks by userPrefs.weeklyStreaks.collectAsState(initial = 1)
    val monthlyStreaks by userPrefs.monthlyStreaks.collectAsState(initial = 0)

    val voiceColor = when (user?.posisi?.lowercase()) {
        "sopran" -> ChoirVoiceSoprano
        "alto" -> ChoirVoiceAlto
        "tenor" -> ChoirVoiceTenor
        "bas" -> ChoirVoiceBass
        else -> ChoirRoyalBlue
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Top Profile Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ChoirNavyDark, ChoirNavy)
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Profile Avatar / Foto
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, ChoirGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.foto != null && user.foto.startsWith("http")) {
                        AsyncImage(
                            model = user.foto,
                            contentDescription = user.namaLengkap,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.logo_choir),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(76.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = user?.namaLengkap ?: "Anggota Sion Choir",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "@${user?.username ?: "member"}",
                    fontSize = 12.sp,
                    color = ChoirGoldLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Voice & Role Badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = voiceColor.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, voiceColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = voiceColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Vokal: ${user?.posisi ?: "Sopran"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        color = ChoirGold.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ChoirGoldLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Badge, contentDescription = null, tint = ChoirGoldLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = user?.role?.uppercase() ?: "MEMBER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChoirGoldLight
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bible Strikes & Achievement Cards
        Text(
            text = "Pencapaian Baca Alkitab",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(2.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = ChoirGoldAmber, modifier = Modifier.size(24.dp))
                    Text(text = "$biblePoints", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ChoirNavy)
                    Text(text = "Total Poin", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(24.dp))
                    Text(text = "$dailyStreak Hari", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFF97316))
                    Text(text = "Strike Harian", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = ChoirRoyalBlue, modifier = Modifier.size(24.dp))
                    Text(text = "$weeklyStreaks Minggu", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ChoirRoyalBlue)
                    Text(text = "Strike Mingguan", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Detail Anggota dari MySQL `chsn_users`
        Text(
            text = "Informasi Anggota (Database chsn_users)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(2.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileInfoRow(icon = Icons.Filled.Person, label = "Nama Lengkap", value = user?.namaLengkap ?: "-")
                Divider(color = ChoirCardBorder)
                ProfileInfoRow(icon = Icons.Filled.Badge, label = "ID Pengguna", value = "#${user?.id ?: 1}")
                Divider(color = ChoirCardBorder)
                ProfileInfoRow(icon = Icons.Filled.Email, label = "Email", value = user?.email ?: "email@ecclesia.org")
                Divider(color = ChoirCardBorder)
                ProfileInfoRow(icon = Icons.Filled.Phone, label = "No. WhatsApp / HP", value = user?.noHp ?: "081234567890")
                Divider(color = ChoirCardBorder)
                ProfileInfoRow(icon = Icons.Filled.MusicNote, label = "Komunitas", value = "JKI Anugrah Ecclesia Cirebon")
            }
        }

        if (onOpenAdmin != null && (user?.role == "admin" || user?.username == "josman")) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ChoirNavyDark
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ChoirGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = ChoirGold.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = ChoirGoldLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Panel Pengelolaan Konten (Admin)",
                                fontWeight = FontWeight.Bold,
                                color = ChoirGoldLight,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Kelola slider, kotbah, materi SATB, jadwal, & anggota",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = ChoirGoldAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = null, tint = ChoirNavyDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buka Panel Admin Choir",
                            color = ChoirNavyDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Logout Button
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
            Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar dari Akun", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun absensi Sion Choir?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            userPrefs.clearSession()
                            showLogoutDialog = false
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Keluar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ChoirNavy, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
