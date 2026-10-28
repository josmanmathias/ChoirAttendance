package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChoirGold
import com.example.ui.theme.ChoirGoldAmber
import com.example.ui.theme.ChoirNavy
import com.example.ui.theme.ChoirNavyDark

enum class MainTab(val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    BERANDA("Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    BIBLE("Bible", Icons.Filled.AutoStories, Icons.Outlined.AutoStories),
    PRESENSI("Presensi", Icons.Filled.QrCodeScanner, Icons.Filled.QrCodeScanner),
    GALERI("Galeri", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary),
    PROFIL("Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun ChoirBottomNavigation(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Beranda
                    NavTabItem(
                        tab = MainTab.BERANDA,
                        isSelected = currentTab == MainTab.BERANDA,
                        onClick = { onTabSelected(MainTab.BERANDA) },
                        modifier = Modifier.weight(1f)
                    )

                    // Tab 2: Bible
                    NavTabItem(
                        tab = MainTab.BIBLE,
                        isSelected = currentTab == MainTab.BIBLE,
                        onClick = { onTabSelected(MainTab.BIBLE) },
                        modifier = Modifier.weight(1f)
                    )

                    // Center space for the raised Presensi button
                    Spacer(modifier = Modifier.weight(1.2f))

                    // Tab 4: Galeri
                    NavTabItem(
                        tab = MainTab.GALERI,
                        isSelected = currentTab == MainTab.GALERI,
                        onClick = { onTabSelected(MainTab.GALERI) },
                        modifier = Modifier.weight(1f)
                    )

                    // Tab 5: Profil
                    NavTabItem(
                        tab = MainTab.PROFIL,
                        isSelected = currentTab == MainTab.PROFIL,
                        onClick = { onTabSelected(MainTab.PROFIL) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Prominent raised circular button in the center
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .offset(y = (-18).dp)
                .size(66.dp)
                .shadow(elevation = 10.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ChoirGoldAmber, ChoirGold, ChoirNavyDark)
                    )
                )
                .clickable { onTabSelected(MainTab.PRESENSI) }
                .testTag("presensi_fab_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "Presensi Choir",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "ABSEN",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    tab: MainTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
            .testTag("nav_tab_${tab.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
            contentDescription = tab.label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.label,
            color = if (isSelected) activeColor else inactiveColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
