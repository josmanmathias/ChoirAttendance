package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.AttendanceMethod
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceType
import com.example.data.model.User
import com.example.data.repository.AttendanceRepository
import com.example.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    user: User?,
    repository: AttendanceRepository,
    onAttendanceSubmitted: (AttendanceRecord) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Step state
    var selectedType by remember { mutableStateOf<AttendanceType?>(null) }
    var selectedMethod by remember { mutableStateOf<AttendanceMethod?>(null) }

    // Location state
    var currentLatitude by remember { mutableStateOf<Double?>(null) }
    var currentLongitude by remember { mutableStateOf<Double?>(null) }
    var distanceToChurchMeters by remember { mutableFloatStateOf(9999f) }
    var simulationModeEnabled by remember { mutableStateOf(false) } // Default to real GPS check

    // Permissions
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasCameraPermission = results[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasLocationPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: hasLocationPermission
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()
        if (!hasCameraPermission) permissions.add(Manifest.permission.CAMERA)
        if (!hasLocationPermission) permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    // Refresh location
    @SuppressLint("MissingPermission")
    fun updateLocation() {
        if (simulationModeEnabled) {
            currentLatitude = repository.churchLocation.latitude
            currentLongitude = repository.churchLocation.longitude
            distanceToChurchMeters = 15f // Within 100m
            return
        }

        if (hasLocationPermission) {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val cts = CancellationTokenSource()
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                    .addOnSuccessListener { loc: Location? ->
                        if (loc != null) {
                            currentLatitude = loc.latitude
                            currentLongitude = loc.longitude
                            distanceToChurchMeters = repository.calculateDistanceMeters(loc.latitude, loc.longitude)
                        } else {
                            fusedClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                                if (lastLoc != null) {
                                    currentLatitude = lastLoc.latitude
                                    currentLongitude = lastLoc.longitude
                                    distanceToChurchMeters = repository.calculateDistanceMeters(lastLoc.latitude, lastLoc.longitude)
                                }
                            }
                        }
                    }
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(simulationModeEnabled, hasLocationPermission) {
        updateLocation()
    }

    val isLocationValid = distanceToChurchMeters <= repository.churchLocation.allowedRadiusMeters

    // Submission result dialog
    var successRecord by remember { mutableStateOf<AttendanceRecord?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    fun submitAttendance() {
        if (!isLocationValid) {
            val dist = distanceToChurchMeters.toInt()
            errorMessage = "Presensi Ditolak! Anda berada di luar radius lokasi Gereja JKI Anugrah Ecclesia Cirebon. Jarak Anda: ${dist}m (Maksimal radius: 100m). Presensi hanya dapat dilakukan di lokasi gereja/latihan."
            return
        }

        val type = selectedType ?: AttendanceType.LATIHAN
        val method = selectedMethod ?: AttendanceMethod.SELFIE
        val uid = user?.id ?: 1

        isSubmitting = true
        coroutineScope.launch {
            val result = repository.submitAttendance(
                userId = uid,
                type = type,
                method = method,
                lat = currentLatitude,
                lng = currentLongitude,
                photoPath = if (method == AttendanceMethod.SELFIE) "selfie_${System.currentTimeMillis()}.jpg" else null
            )
            isSubmitting = false
            result.onSuccess { rec ->
                successRecord = rec
            }.onFailure { err ->
                errorMessage = err.message ?: "Gagal mengirim data absensi. Silakan coba lagi."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PRESENSI CHOIR",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.Close, contentDescription = "Batal", tint = Color.White)
                    }
                },
                actions = {
                    // Test Simulation Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Simulasi GPS", fontSize = 10.sp, color = ChoirGoldLight)
                        Switch(
                            checked = simulationModeEnabled,
                            onCheckedChange = { simulationModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ChoirGoldAmber,
                                checkedTrackColor = ChoirGoldSurface
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ChoirNavy
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Geofence Radar Status Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isLocationValid) SuccessGreen.copy(alpha = 0.12f) else WarningOrange.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isLocationValid) SuccessGreen.copy(alpha = 0.4f) else WarningOrange.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLocationValid) Icons.Filled.CheckCircle else Icons.Filled.LocationOff,
                        contentDescription = null,
                        tint = if (isLocationValid) SuccessGreen else WarningOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isLocationValid) "Lokasi Valid di Area JKI Ecclesia" else "Di Luar Radius 100m Gereja",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocationValid) SuccessGreen else WarningOrange
                        )
                        Text(
                            text = "Jarak: ${distanceToChurchMeters.toInt()}m (Maks: 100m) • Lat: ${currentLatitude ?: -6.728251}, Lng: ${currentLongitude ?: 108.553246}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { updateLocation() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh GPS", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // STEP 1: PILIH JENIS ABSEN (LATIHAN / IBADAH)
            if (selectedType == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Langkah 1: Pilih Jenis Absensi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Silakan tentukan jenis kegiatan choir hari ini:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Card 1: ABSEN LATIHAN
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clickable { selectedType = AttendanceType.LATIHAN }
                            .testTag("option_absen_latihan"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ChoirRoyalBlue)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(ChoirRoyalBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.LibraryMusic, contentDescription = null, tint = ChoirRoyalBlue, modifier = Modifier.size(30.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "ABSEN LATIHAN",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ChoirRoyalBlue
                                )
                                Text(
                                    text = "Latihan rutin paduan suara / gladi resik",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card 2: ABSEN IBADAH
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clickable { selectedType = AttendanceType.IBADAH }
                            .testTag("option_absen_ibadah"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ChoirGold)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(ChoirGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Church, contentDescription = null, tint = ChoirGold, modifier = Modifier.size(30.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "ABSEN IBADAH",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ChoirGold
                                )
                                Text(
                                    text = "Pelayanan Ibadah Raya Minggu / Hari Khusus",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else if (selectedMethod == null) {
                // STEP 2: PILIH METODE (SELFIE / QR CODE)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Langkah 2: Pilih Metode Absen",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Jenis: ${selectedType?.label}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChoirGold
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Method 1: Selfie
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .shadow(3.dp, RoundedCornerShape(14.dp))
                            .clickable { selectedMethod = AttendanceMethod.SELFIE }
                            .testTag("method_selfie"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CameraFront, contentDescription = null, tint = ChoirNavy, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Absen dengan Selfie", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("Buka kamera depan untuk verifikasi wajah", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Method 2: QR Code
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .shadow(3.dp, RoundedCornerShape(14.dp))
                            .clickable { selectedMethod = AttendanceMethod.QR_CODE }
                            .testTag("method_qr_code"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = ChoirGoldAmber, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Absen dengan QR Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("Scan barcode di meja latihan/panggung", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = { selectedType = null }) {
                        Text("← Ganti Jenis Absen")
                    }
                }
            } else {
                // STEP 3: ACTIVE CAMERA VIEW (SELFIE / QR CODE)
                Box(modifier = Modifier.fillMaxSize()) {
                    val cameraSelector = if (selectedMethod == AttendanceMethod.SELFIE) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }

                    if (hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview
                                        )
                                    } catch (_: Exception) {}
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Izin Kamera Diperlukan", color = Color.White)
                        }
                    }

                    // Overlay for QR Code (Square Frame with scanning line)
                    if (selectedMethod == AttendanceMethod.QR_CODE) {
                        val infiniteTransition = rememberInfiniteTransition()
                        val laserOffset by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 240f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Arahkan Kamera ke QR Code Gereja",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                // Square Box Target
                                Box(
                                    modifier = Modifier
                                        .size(240.dp)
                                        .border(3.dp, ChoirGoldAmber, RoundedCornerShape(16.dp))
                                        .background(Color.Transparent)
                                ) {
                                    // Animated laser scanner bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(y = laserOffset.dp)
                                            .height(3.dp)
                                            .background(ChoirGoldAmber)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Auto trigger or test scan button
                                Button(
                                    onClick = { submitAttendance() },
                                    enabled = isLocationValid && !isSubmitting,
                                    colors = ButtonDefaults.buttonColors(containerColor = ChoirGoldAmber)
                                ) {
                                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = ChoirNavyDark)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simulasi QR Cocok (Kirim Absen)", color = ChoirNavyDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Overlay for Selfie (Bottom action button)
                    if (selectedMethod == AttendanceMethod.SELFIE) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                    )
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Posisikan wajah Anda dengan jelas",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { submitAttendance() },
                                    enabled = isLocationValid && !isSubmitting,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("kirim_absen_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isLocationValid) ChoirGoldAmber else Color.Gray
                                    )
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = ChoirNavyDark, modifier = Modifier.size(24.dp))
                                    } else {
                                        Icon(Icons.Filled.Camera, contentDescription = null, tint = ChoirNavyDark)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "KIRIM ABSEN SEKARANG",
                                            color = ChoirNavyDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
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

    // Success dialog
    if (successRecord != null) {
        val rec = successRecord!!
        AlertDialog(
            onDismissRequest = {
                onAttendanceSubmitted(rec)
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Absensi Berhasil!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Data absensi Anda telah berhasil dicatat dan dikirim ke server.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Kegiatan: ${rec.type.label}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Metode: ${rec.method.label}", fontSize = 12.sp)
                            Text("Tanggal: ${rec.tanggal}", fontSize = 12.sp)
                            Text("Waktu: ${rec.waktu} WIB", fontWeight = FontWeight.Bold, color = ChoirRoyalBlue, fontSize = 12.sp)
                            Text("Lokasi: Latitude ${rec.latitude}, Longitude ${rec.longitude}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAttendanceSubmitted(rec)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Lihat Rekap Absensi", color = Color.White)
                }
            }
        )
    }

    // Error Dialog (Rejection due to out-of-radius or duplicate check-in)
    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Peringatan",
                    tint = WarningOrange,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Presensi Tidak Dapat Diproses",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = msg,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = { errorMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ChoirNavy)
                ) {
                    Text("Tutup", color = Color.White)
                }
            }
        )
    }
}

private fun Modifier.scale(scale: Float): Modifier = this
