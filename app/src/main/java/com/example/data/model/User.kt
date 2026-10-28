package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: Int,
    @Json(name = "username") val username: String,
    @Json(name = "nama_lengkap") val namaLengkap: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "no_hp") val noHp: String? = null,
    @Json(name = "posisi") val posisi: String = "Sopran", // 'Sopran', 'Alto', 'Tenor', 'Bas'
    @Json(name = "role") val role: String = "member",     // 'member', 'admin'
    @Json(name = "foto") val foto: String? = null,
    @Json(name = "status") val status: String = "active", // 'active', 'inactive'
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

enum class VoicePosition(val label: String) {
    SOPRAN("Sopran"),
    ALTO("Alto"),
    TENOR("Tenor"),
    BAS("Bas")
}
