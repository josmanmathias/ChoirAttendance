package com.example.data.local

import com.example.data.model.GalleryItem
import com.example.data.model.MateriItem
import com.example.data.model.PosterItem
import com.example.data.model.SermonCategory
import com.example.data.model.SermonItem

object MediaDataProvider {

    val sliderPosters = listOf(
        PosterItem(
            id = "p1",
            title = "Jadwal Pelayanan Ibadah Raya Minggu",
            subtitle = "Sion Choir JKI Anugrah Ecclesia Cirebon",
            imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80",
            actionText = "Lihat Jadwal"
        ),
        PosterItem(
            id = "p2",
            title = "Latihan Rutin Choir Setiap Kamis",
            subtitle = "Pukul 19.00 WIB di Ruang Sion Ecclesia",
            imageUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?auto=format&fit=crop&w=800&q=80",
            actionText = "Info Latihan"
        ),
        PosterItem(
            id = "p3",
            title = "Praise & Worship Night - Glory to God",
            subtitle = "Persembahan Pujian Akbar Bersama Seluruh Anggota Choir",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80",
            actionText = "Rincian Lagu"
        ),
        PosterItem(
            id = "p4",
            title = "Gerakan Baca Firman Tiap Hari",
            subtitle = "Selesaikan 5 Pasal Per Hari dan Dapatkan Badge Strike!",
            imageUrl = "https://images.unsplash.com/photo-1504052434569-70ad5836ab65?auto=format&fit=crop&w=800&q=80",
            actionText = "Mulai Baca"
        )
    )

    val sermons = listOf(
        // Kotbah Ibadah Raya
        SermonItem(
            id = "s1",
            title = "Menjadi Penyembah yang Benar dalam Roh dan Kebenaran",
            preacher = "Pdt. JKI Anugrah Ecclesia",
            date = "Minggu, 25 Okt 2026",
            duration = "45:20",
            youtubeId = "dQw4w9WgXcQ", // Playable YouTube ID
            category = SermonCategory.IBADAH_RAYA
        ),
        SermonItem(
            id = "s2",
            title = "Kuasa Pujian yang Meruntuhkan Tembok Yerikho",
            preacher = "Ev. Tim Pelayanan Sion",
            date = "Minggu, 18 Okt 2026",
            duration = "42:15",
            youtubeId = "3JZ_D3ELwOQ",
            category = SermonCategory.IBADAH_RAYA
        ),
        SermonItem(
            id = "s3",
            title = "Hati yang Berkenan di Hadapan Hadirat-Nya",
            preacher = "Pdt. JKI Anugrah Ecclesia",
            date = "Minggu, 11 Okt 2026",
            duration = "50:00",
            youtubeId = "kJQP7kiw5Fk",
            category = SermonCategory.IBADAH_RAYA
        ),

        // Kotbah PW Pagi
        SermonItem(
            id = "pw1",
            title = "Embun Pagi: Kesegaran Baru Tiap Hari",
            preacher = "Pst. Doa Fajar Ecclesia",
            date = "Sabtu, 24 Okt 2026",
            duration = "28:10",
            youtubeId = "9bZkp7q19f0",
            category = SermonCategory.PW_PAGI
        ),
        SermonItem(
            id = "pw2",
            title = "Memuji Tuhan di Saat Fajar Menyingsing",
            preacher = "Tim Doa & Penyembahan",
            date = "Sabtu, 17 Okt 2026",
            duration = "31:45",
            youtubeId = "fJ9rUzIMcZQ",
            category = SermonCategory.PW_PAGI
        ),

        // Pesan Gembala
        SermonItem(
            id = "pg1",
            title = "Pesan Gembala: Kesatuan Hati Tim Sion Choir",
            preacher = "Gembala Sidang JKI Anugrah Ecclesia Cirebon",
            date = "Bulan Oktober 2026",
            duration = "18:40",
            youtubeId = "L_LUpnjgPso",
            category = SermonCategory.PESAN_GEMBALA
        ),
        SermonItem(
            id = "pg2",
            title = "Pesan Gembala: Mempersiapkan Persembahan Terbaik",
            preacher = "Gembala Sidang JKI Anugrah Ecclesia Cirebon",
            date = "Bulan September 2026",
            duration = "22:15",
            youtubeId = "RgKAFK3878w",
            category = SermonCategory.PESAN_GEMBALA
        )
    )

    val materiPelayanan = listOf(
        MateriItem(
            id = "m1",
            title = "Kudus, Kuduslah Tuhan (Sanctus)",
            subtitle = "Partitur 4 Suara (SATB) - Aransemen Ibadah Raya",
            voicePart = "Semua",
            pdfUrl = "https://terbangcirebon.sch.id/jki/ministry/choir/materi/sanctus_satb.pdf",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            lyrics = "Kudus, kuduslah Tuhan Allah semesta alam\nBumi dan surga penuh kemuliaan-Mu\nHosana di tempat yang maha tinggi\nDiberkatilah yang datang dalam nama Tuhan."
        ),
        MateriItem(
            id = "m2",
            title = "Kudus, Kuduslah Tuhan - Latihan Suara 1 (Sopran)",
            subtitle = "Fokus Nada Tinggi & Artikulasi Melodi Utama",
            voicePart = "Sopran",
            pdfUrl = "https://terbangcirebon.sch.id/jki/ministry/choir/materi/sanctus_sopran.pdf",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            lyrics = "Melodi Sopran: C5 - G5 - A5 - F5"
        ),
        MateriItem(
            id = "m3",
            title = "Kudus, Kuduslah Tuhan - Latihan Suara 2 (Alto)",
            subtitle = "Harmonisasi Suara Dalam & Pernafasan Diafragma",
            voicePart = "Alto",
            pdfUrl = "https://terbangcirebon.sch.id/jki/ministry/choir/materi/sanctus_alto.pdf",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            lyrics = "Harmoni Alto: G4 - E4 - F4 - D4"
        ),
        MateriItem(
            id = "m4",
            title = "Kudus, Kuduslah Tuhan - Latihan Suara 3 (Tenor)",
            subtitle = "Kekuatan Resonansi & Interval Suara Tenor",
            voicePart = "Tenor",
            pdfUrl = "https://terbangcirebon.sch.id/jki/ministry/choir/materi/sanctus_tenor.pdf",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            lyrics = "Harmoni Tenor: E4 - B3 - C4 - A3"
        ),
        MateriItem(
            id = "m5",
            title = "Kudus, Kuduslah Tuhan - Latihan Suara 4 (Bas)",
            subtitle = "Fondasi Nada Dasar & Ritme Ketukan",
            voicePart = "Bas",
            pdfUrl = "https://terbangcirebon.sch.id/jki/ministry/choir/materi/sanctus_bas.pdf",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            lyrics = "Fondasi Bass: C3 - G2 - A2 - F2"
        ),
        MateriItem(
            id = "m6",
            title = "Agunglah Karya-Mu (Great Is Thy Faithfulness)",
            subtitle = "Persembahan Pujian Khusus Natal & Paskah",
            voicePart = "Semua",
            pdfUrl = "https://terbangcirebon.sch.id/jki/ministry/choir/materi/great_faithfulness.pdf",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            lyrics = "Besar setia-Mu, Allah Bapaku, tiada bayang perobahan pada-Mu..."
        )
    )

    val galleryItems = listOf(
        GalleryItem(
            id = "g1",
            title = "Pelayanan Ibadah Raya Minggu Ceria",
            date = "25 Oktober 2026",
            imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=600&q=80",
            description = "Sion Choir melayani persembahan pujian di hadapan jemaat JKI Anugrah Ecclesia Cirebon."
        ),
        GalleryItem(
            id = "g2",
            title = "Latihan Vokal & Pembagian Suara SATB",
            date = "22 Oktober 2026",
            imageUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?auto=format&fit=crop&w=600&q=80",
            description = "Kebersamaan dan semangat latihan rutin di ruang Sion Ecclesia."
        ),
        GalleryItem(
            id = "g3",
            title = "Perayaan HUT JKI Anugrah Ecclesia",
            date = "15 September 2026",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80",
            description = "Penampilan paduan suara agung gabungan seluruh sektor pelayanan."
        ),
        GalleryItem(
            id = "g4",
            title = "Fellowship & Retreat Anggota Choir",
            date = "18 Agustus 2026",
            imageUrl = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=600&q=80",
            description = "Membangun keakraban, kesatuan kasih, dan saling menguatkan dalam melayani Tuhan."
        ),
        GalleryItem(
            id = "g5",
            title = "Gladi Resik Ibadah Paduan Suara",
            date = "10 Juli 2026",
            imageUrl = "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=600&q=80",
            description = "Persiapan mikrofon dan posisi panggung bersama tim multimedia dan soundman gereja."
        ),
        GalleryItem(
            id = "g6",
            title = "Praise & Worship Youth Choir",
            date = "28 Juni 2026",
            imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80",
            description = "Semangat generasi muda memuji Tuhan dengan segenap talenta dan kesungguhan."
        )
    )
}
