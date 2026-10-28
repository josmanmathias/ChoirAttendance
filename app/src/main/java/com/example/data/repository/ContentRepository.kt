package com.example.data.repository

import com.example.data.local.MediaDataProvider
import com.example.data.model.GalleryItem
import com.example.data.model.MateriItem
import com.example.data.model.PosterItem
import com.example.data.model.SermonCategory
import com.example.data.model.SermonItem
import com.example.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ContentRepository {

    private val _posters = MutableStateFlow<List<PosterItem>>(MediaDataProvider.sliderPosters)
    val posters: StateFlow<List<PosterItem>> = _posters.asStateFlow()

    private val _sermons = MutableStateFlow<List<SermonItem>>(MediaDataProvider.sermons)
    val sermons: StateFlow<List<SermonItem>> = _sermons.asStateFlow()

    private val _materi = MutableStateFlow<List<MateriItem>>(MediaDataProvider.materiPelayanan)
    val materi: StateFlow<List<MateriItem>> = _materi.asStateFlow()

    private val _gallery = MutableStateFlow<List<GalleryItem>>(MediaDataProvider.galleryItems)
    val gallery: StateFlow<List<GalleryItem>> = _gallery.asStateFlow()

    private val _jadwalUrl = MutableStateFlow("https://terbangcirebon.sch.id/jki/ministry/choir/jadwal.png")
    val jadwalUrl: StateFlow<String> = _jadwalUrl.asStateFlow()

    private val _members = MutableStateFlow<List<User>>(
        listOf(
            User(
                id = 1,
                username = "josman",
                namaLengkap = "Josman Lily",
                email = "josmanlily@gmail.com",
                noHp = "081234567890",
                posisi = "Tenor",
                role = "admin",
                foto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                status = "active"
            ),
            User(
                id = 2,
                username = "mariagrace",
                namaLengkap = "Maria Grace",
                email = "maria@example.com",
                noHp = "081987654321",
                posisi = "Sopran",
                role = "member",
                foto = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=300&q=80",
                status = "active"
            ),
            User(
                id = 3,
                username = "danieltan",
                namaLengkap = "Daniel Tanuwijaya",
                email = "daniel@example.com",
                noHp = "085612345678",
                posisi = "Bas",
                role = "member",
                foto = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80",
                status = "active"
            ),
            User(
                id = 4,
                username = "esternovita",
                namaLengkap = "Ester Novita",
                email = "ester@example.com",
                noHp = "087812348765",
                posisi = "Alto",
                role = "member",
                foto = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80",
                status = "active"
            ),
            User(
                id = 5,
                username = "jonathan",
                namaLengkap = "Jonathan Edward",
                email = "jonathan@example.com",
                noHp = "081399887766",
                posisi = "Tenor",
                role = "member",
                foto = null,
                status = "active"
            )
        )
    )
    val members: StateFlow<List<User>> = _members.asStateFlow()

    // --- Poster / Slider Operations ---
    fun addPoster(title: String, subtitle: String, imageUrl: String, actionText: String = "Lihat Detail") {
        val newPoster = PosterItem(
            id = "p_${UUID.randomUUID().toString().take(6)}",
            title = title,
            subtitle = subtitle,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80" },
            actionText = actionText
        )
        _posters.value = listOf(newPoster) + _posters.value
    }

    fun deletePoster(id: String) {
        _posters.value = _posters.value.filter { it.id != id }
    }

    // --- Sermon Operations ---
    fun addSermon(title: String, preacher: String, date: String, duration: String, youtubeIdOrUrl: String, category: SermonCategory) {
        val cleanYoutubeId = extractYoutubeId(youtubeIdOrUrl)
        val newSermon = SermonItem(
            id = "s_${UUID.randomUUID().toString().take(6)}",
            title = title,
            preacher = preacher,
            date = date,
            duration = duration.ifBlank { "40:00" },
            youtubeId = cleanYoutubeId,
            category = category
        )
        _sermons.value = listOf(newSermon) + _sermons.value
    }

    fun deleteSermon(id: String) {
        _sermons.value = _sermons.value.filter { it.id != id }
    }

    // --- Materi Pelayanan Operations ---
    fun addMateri(title: String, subtitle: String, voicePart: String, pdfUrl: String?, audioUrl: String?, lyrics: String?) {
        val newMateri = MateriItem(
            id = "m_${UUID.randomUUID().toString().take(6)}",
            title = title,
            subtitle = subtitle,
            voicePart = voicePart,
            pdfUrl = pdfUrl,
            audioUrl = audioUrl,
            lyrics = lyrics
        )
        _materi.value = listOf(newMateri) + _materi.value
    }

    fun deleteMateri(id: String) {
        _materi.value = _materi.value.filter { it.id != id }
    }

    // --- Gallery Operations ---
    fun addGalleryItem(title: String, date: String, imageUrl: String, description: String) {
        val newItem = GalleryItem(
            id = "g_${UUID.randomUUID().toString().take(6)}",
            title = title,
            date = date,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80" },
            description = description
        )
        _gallery.value = listOf(newItem) + _gallery.value
    }

    fun deleteGalleryItem(id: String) {
        _gallery.value = _gallery.value.filter { it.id != id }
    }

    // --- Jadwal Operations ---
    fun updateJadwalUrl(url: String) {
        if (url.isNotBlank()) {
            _jadwalUrl.value = url
        }
    }

    // --- Members Operations ---
    fun updateMember(userId: Int, posisi: String, role: String, status: String) {
        _members.value = _members.value.map { u ->
            if (u.id == userId) u.copy(posisi = posisi, role = role, status = status)
            else u
        }
    }

    fun addMember(nama: String, username: String, posisi: String, role: String, noHp: String?) {
        val newUid = (_members.value.maxOfOrNull { it.id } ?: 10) + 1
        val newUser = User(
            id = newUid,
            username = username.lowercase().trim(),
            namaLengkap = nama.trim(),
            email = "$username@ecclesia.org",
            noHp = noHp ?: "0812345678",
            posisi = posisi,
            role = role,
            foto = null,
            status = "active"
        )
        _members.value = _members.value + newUser
    }

    private fun extractYoutubeId(input: String): String {
        val trimmed = input.trim()
        if (trimmed.contains("v=")) {
            return trimmed.substringAfter("v=").substringBefore("&")
        }
        if (trimmed.contains("youtu.be/")) {
            return trimmed.substringAfter("youtu.be/").substringBefore("?")
        }
        return trimmed
    }
}
