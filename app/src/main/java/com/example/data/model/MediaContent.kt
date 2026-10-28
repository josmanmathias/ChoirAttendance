package com.example.data.model

enum class SermonCategory(val label: String) {
    IBADAH_RAYA("Kotbah Ibadah Raya"),
    PW_PAGI("Kotbah PW Pagi"),
    PESAN_GEMBALA("Pesan Gembala")
}

data class SermonItem(
    val id: String,
    val title: String,
    val preacher: String,
    val date: String,
    val duration: String,
    val youtubeId: String,
    val category: SermonCategory
)

data class MateriItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val voicePart: String = "Semua", // Semua, Sopran, Alto, Tenor, Bas
    val pdfUrl: String? = null,
    val audioUrl: String? = null,
    val lyrics: String? = null
)

data class GalleryItem(
    val id: String,
    val title: String,
    val date: String,
    val imageUrl: String,
    val description: String
)

data class PosterItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val actionText: String = "Lihat Detail"
)
