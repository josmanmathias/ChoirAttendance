package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class AttendanceType(val label: String, val tableName: String) {
    LATIHAN("Absen Latihan", "chsn_absensi_latihan"),
    IBADAH("Absen Ibadah", "chsn_absensi_ibadah")
}

enum class AttendanceMethod(val label: String) {
    SELFIE("Selfie"),
    QR_CODE("QR Code")
}

@JsonClass(generateAdapter = true)
data class AttendanceRecord(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "foto") val foto: String? = null,
    @Json(name = "tanggal") val tanggal: String, // YYYY-MM-DD
    @Json(name = "waktu") val waktu: String,     // HH:MM:SS
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "type") val type: AttendanceType = AttendanceType.LATIHAN,
    @Json(name = "method") val method: AttendanceMethod = AttendanceMethod.SELFIE,
    @Json(name = "created_at") val createdAt: String? = null
)

data class ChurchLocation(
    val latitude: Double = -6.728251,
    val longitude: Double = 108.553246,
    val allowedRadiusMeters: Float = 100f,
    val churchName: String = "JKI Anugrah Ecclesia Cirebon"
)
